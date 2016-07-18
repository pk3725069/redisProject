package com.util.common.redis;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.util.common.ApplicationContextUtil;
import com.util.common.SerializeUtil;



public class SpringRedisUtil {

	
	@SuppressWarnings("unchecked")
	private static RedisTemplate<Serializable, Serializable> redisTemplate = 
				(RedisTemplate<Serializable, Serializable>) ApplicationContextUtil
						.getBean("redisTemplate");
	 /**
	  * 反射通过属性名获取属性值
	  * @param fieldName
	  * @param o
	  * @return
	  */
	 private static Object  getFieldValueByName(String fieldName, Object o) {  
	       try {    
	           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
	           String getter = "get" + firstLetter + fieldName.substring(1);    
	           Method method = o.getClass().getMethod(getter, new Class[] {});    
	           Object value = method.invoke(o, new Object[] {});    
	           return value;    
	       } catch (Exception e) {    
	          // log.error(e.getMessage(),e);    
	           return null;    
	       }    
	   }
	 /**
	  * 通过老的对象值给新的对象设置值
	  * @param fieldName
	  * @param object
	  * @param oldObject
	  */
	 private static void setFieldValueByOldObject(String fieldName, Object object,Object oldObject){
		 try {    
	           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
	           String getter = "get" + firstLetter + fieldName.substring(1);    
	           Method oldMethod = oldObject.getClass().getMethod(getter, new Class[] {});    
	           Object oldValue = oldMethod.invoke(oldObject, new Object[] {});  
	           
	           String setter = "set" + firstLetter + fieldName.substring(1);    
	           Method method = object.getClass().getMethod(setter, new Class[] {oldValue.getClass()});  
	           method.invoke(object,  new Object[]{oldValue});  
	       } catch (Exception e) {    
	          // log.error(e.getMessage(),e);    
	           return ;    
	       }    
	 }
	public static void save(final String key, Object value) {
		final byte[] vbytes = SerializeUtil.serialize(value);
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				connection.set(redisTemplate.getStringSerializer().serialize(key), vbytes);
				return null;
			}
		});
	}
 
	public static <T> T get(final String key, Class<T> elementType) {
		return redisTemplate.execute(new RedisCallback<T>() {
			@Override
			public T doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] keybytes = redisTemplate.getStringSerializer().serialize(key);
				if (connection.exists(keybytes)) {
					byte[] valuebytes = connection.get(keybytes);
					@SuppressWarnings("unchecked")
					T value = (T) SerializeUtil.unserialize(valuebytes);
					return value;
				}
				return null;
			}
		});
	}
	   public static void delete(final String keys) {  
	       redisTemplate.delete(keys);  
	   }  
	   
	    public static <T>  void update(final String key, Object value,Class<T> elementType) { 
	    	T oldValue =  get(key,elementType);
	    	if(oldValue==null)
	    	{
	    		System.out.println("Redis don't has this key!");
	    		return ;
	    	}
		    Field[] fields=value.getClass().getDeclaredFields(); 
		    Field[] oldFields=oldValue.getClass().getDeclaredFields(); 
		    if(fields.length!=oldFields.length)
		    {
		    	System.out.println("key's object is not the same as update's Object !");
		    	return ;
		    }
		    
			for(int i=0;i<fields.length;i++)
			{
				//new为空 同时old不为空
				if(getFieldValueByName(fields[i].getName(), value)==null&&getFieldValueByName(fields[i].getName(), oldValue)!=null)
				{
					setFieldValueByOldObject(fields[i].getName(),value,oldValue);
					System.out.println("value : "+getFieldValueByName(fields[i].getName(), value));
				}
				//getFieldValueByName(Oldfields[i].getName(), value);
			}
			
			
			final byte[] vbytes = SerializeUtil.serialize(value);
			redisTemplate.execute(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection)
						throws DataAccessException {
					connection.set(redisTemplate.getStringSerializer().serialize(key), vbytes);
					return null;
				}
			});
	   }  
}