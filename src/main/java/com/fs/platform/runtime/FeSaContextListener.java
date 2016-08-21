package com.fs.platform.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.fs.platform.commons.annotation.FeSaComponent;
import com.fs.platform.commons.annotation.FeSaService;

@WebListener
public class FeSaContextListener implements ServletContextAttributeListener,ServletContextListener{ 
	 
	 
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("===上下文销毁===");
	}

	public void contextInitialized(ServletContextEvent event) {
		System.out.println("===上下文初始化===");
		ServletContext context= event.getServletContext();
		context.getInitParameter("");
		try {
			List<Class<?>> classes= ClassUtil.getClasses("com.fs.app");
			Map<String,Class> classImpl=new HashMap<String,Class>();
			//1.获取所有接口显示类
			for (Class class1 : classes) {
				Annotation[] annotations=class1.getAnnotations();
				for (Annotation annotation : annotations) {
					if(annotation.annotationType()==FeSaComponent.class){
						classImpl.put(class1.getInterfaces()[0].getName(), class1);
						System.out.println("===fscomponent:实现类"+class1.getName()+"接口:"+class1.getInterfaces()[0].getName());
					}
				}
			}
			//2.获取每个类中的属性字段
			for (Class class1 : classes) {
				Field[] fields= class1.getDeclaredFields();
				for (Field field : fields) {
					if(field.isAnnotationPresent(FeSaService.class)){
						System.out.println("===fsservice:"+field.getType().getName());
						field.setAccessible(true);
						Class classtag= classImpl.get(field.getType().getName());
						try {
							Object obj= Class.forName(classtag.getName(), false, class1.getClassLoader()).newInstance();
							field.set(class1.newInstance(), obj);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("===上下文初始化完成===");
	}

	public void attributeAdded(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
