package com.cuijb.web.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XMLTest {

	// 解析XML
	private static ArrayList<Channel> getChannel() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		// 获取DOM解析器
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc;
			doc = builder.parse(new File("D:/xml.xml"));
			// 得到一个element根元素,获得根节点
			Element root = doc.getDocumentElement();
			System.out.println("根元素：" + root.getNodeName());

			// 子节点
			NodeList personNodes = root.getElementsByTagName("Channel");
			// 获取channel的总个数
			System.out.println("channel节点总数：" + personNodes.getLength());
			for (int i = 0; i < personNodes.getLength(); i++) {
				Element personElement = (Element) personNodes.item(i);
				log.info("channel: id:{}, name:{}, status:{}, type:{}", personElement.getAttribute("id"),
						personElement.getAttribute("name"), personElement.getAttribute("status"),
						personElement.getAttribute("channelType"));
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return list;
	}

	static String sqlStr = "jdbc:mysql://localhost:3306/dpsdkdemo";
	static String rootName = "root";// 数据库名
	static String rootPwd = "root";// 数据库密码

	// public static void writeToMysql(Channel channel) {
	// //System.out.println(channel);
	// //1.加载driver驱动
	// try {
	// // 加载MySql的驱动类
	// Class.forName("com.mysql.jdbc.Driver");
	// } catch (ClassNotFoundException e) {
	// System.out.println("找不到驱动程序类 ，加载驱动失败！");
	// e.printStackTrace();
	// }
	// //2.建立连接
	// Statement st = null;
	// //调用DriverManager对象的getConnection()方法，获得一个Connection对象
	// Connection con =null;
	// try {
	// //建立数据库连接
	// con = (Connection) DriverManager.getConnection(sqlStr, rootName,rootPwd);
	// String id = channel.getId();
	// String name = channel.getName();
	// String status = channel.getStatus();
	// String channelType = channel.getChannelType();
	// //插入语句格式
	// String sql = "insert into Channel(id,name,status,channelType)
	// values(\""+id+"\",\""+name+"\",\""+status+"\",\""+channelType+"\")";
	// // System.out.println(sql);
	// st = (Statement) con.createStatement(); //创建一个Statement对象
	// st.executeUpdate(sql);//提交数据更新
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }finally{
	// try {
	// st.close();
	// con.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// 测试方法
	@Test
	public void aaa() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		list = getChannel();
		int i;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				// writeToMysql(list.get(i));
			}

		}
		System.out.println("共插入数据" + i + "条");
	}

	@Test
	public void bbb() {
		String[] aaa = new String[0];
		log.debug("aaa[0] is {}", aaa[0]);
	}
}
