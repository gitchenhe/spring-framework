package org.springframework.jdbc;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author chenhe
 * @date 2019-09-20 13:50
 * @desc
 */
public class MyDataSourceTransactionManagerTests {
	private MysqlDataSource ds;

	private Connection con;

	private DataSourceTransactionManager tm;


	@Before
	public void before() throws SQLException {
		ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://aliyun:1102/test?useSSL=false&serverTimezone=UTC");
		ds.setUser("root");
		ds.setPassword("chen1993");
		con = ds.getConnection();
		tm = new DataSourceTransactionManager();
		tm.setDataSource(ds);
				//DriverManager.getConnection("jdbc:mysql://aliyun:1102/test?useSSL=false&serverTimezone=UTC","root","chen1993");

	}

	@After
	public void after() throws SQLException {
		con.close();
	}

	@Test
	public void test(){
		TransactionTemplate tt = new TransactionTemplate(tm);
		tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
		tt.getTransactionManager().getTransaction(tt);

		tt.execute(transactionStatus -> {


			Connection connection = DataSourceUtils.getConnection(ds);
			try {
				ResultSet resultSet = connection.prepareStatement("select * from admin").executeQuery();
				while (resultSet.next()){
					System.out.println(resultSet.getObject(1) + " , " + resultSet.getObject(2) + " , " + resultSet.getObject(3));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			connection = DataSourceUtils.getConnection(ds);
			try {
				ResultSet resultSet = connection.prepareStatement("select * from admin").executeQuery();
				while (resultSet.next()){
					System.out.println(resultSet.getObject(1) + " , " + resultSet.getObject(2) + " , " + resultSet.getObject(3));
				}
				throw new RuntimeException("回滚吧!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		});


	}

}
