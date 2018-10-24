package com.lmg.mybatis.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.lmg.mybatis.bean.Employee;
import com.lmg.mybatis.bean.EmployeeExample;
import com.lmg.mybatis.bean.EmployeeExample.Criteria;
import com.lmg.mybatis.dao.EmployeeMapper;

public class MyBatisTest {
	
	private SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-conf.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}
	@Test
	public void testMGB() throws Exception{
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		File configFile = new File("mbg.xml");
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}
	
	@Test
	public void testMybatis3Simple() throws Exception{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			List<Employee> list = mapper.selectByExample(null);
			
			for (Employee employee : list) {
				System.out.println(employee.getLastName());
			}
		} finally {
			openSession.close();
		}
	}
	
	@Test
	public void testMybatis3() throws Exception{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			//xxxExample 就是封装查询条件的
			//1.查询所有
			List<Employee> list = mapper.selectByExample(null);
			//2.查询员工名字中有字母e的,和员工性别是1的
			//封装员工查询条件的example
			EmployeeExample example = new EmployeeExample();
			//创建一个Criteria,这个Criteria 就是拼装查询条件
			//select id, last_name, email, gender, d_id from tbl_employee 
			//WHERE ( last_name like ? and gender = ? ) or email like "%e%"
			Criteria criteria = example.createCriteria();
			criteria.andLastNameLike("%e%");
			criteria.andGenderEqualTo("1");
			
			Criteria criteria2 = example.createCriteria();
			criteria2.andEmailLike("%e%");
			example.or(criteria2);
			List<Employee> list2 = mapper.selectByExample(example);
			for (Employee employee : list2) {
				System.out.println(employee.getLastName());
			}
		} finally {
			openSession.close();
		}
	}
}
