package com.test;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.util.common.redis.SpringRedisUtil;
import com.x.entity.User;
  
/**  
 * ≤‚ ‘ 
 * @author http://blog.csdn.net/java2000_wl  
 * @version <b>1.0</b>  
 */    
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})  
public class RedisTest extends AbstractJUnit4SpringContextTests {  

    @Test
    public void testAddUser() {  
        User user = new User();  
        user.setId("user3");  
        user.setName("java2000_wl");  
        user.setPassword("java2000_wl");  
        SpringRedisUtil.save(user.getId(),user);  
        System.out.println(user);
    } 
    
    /** 
     * ªÒ»° 
     * <br>------------------------------<br> 
     */  
    @Test  
    public void testGetUser() { 
        String id = "user3";  
    	User user = SpringRedisUtil.get(id, User.class);
    	System.out.println(user.toString());
    }  
    @Test
    public void testUpdateUser(){
    	String id = "user3";  
		User user = new User();  
	    user.setName("java_wl");  
    	SpringRedisUtil.update(id,user, User.class);
    }
    @Test
    public void testDeleteUser(){
    	String id = "user3";  
    	SpringRedisUtil.delete(id);
    }

}  