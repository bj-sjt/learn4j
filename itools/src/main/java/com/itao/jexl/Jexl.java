package com.itao.jexl;

import org.apache.commons.jexl2.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jexl {
    public static Object invokeMethod(String jexlExp, Map<String, Object> map) {
        JexlEngine jexl = new JexlEngine();
        Script e =  jexl.createScript(jexlExp);
        JexlContext jc = new MapContext();
        for (String key : map.keySet()) {
            jc.set(key, map.get(key));
        }
        if (null == e.execute(jc)) {
            return "";
        }
        return e.execute(jc);
    }

    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("testService",new TestService());
        map.put("person",new Person());
        String expression="testService.save(person)";
        Object o = Jexl.invokeMethod(expression, map);
        System.out.println(o);
        /*Object o = Jexl.invokeMethod("i=0;if(true){i=2} i*2;", new HashMap<>());
        System.out.println(o);*/
        //描述一个人，他有两条腿
        //testJexl();

    }

    private static void testJexl() {
        Map<String, Object> person = new HashMap<>();
        person.put("skinColor", "red");//皮肤为红色
        person.put("age", 23);//年龄23
        person.put("cash", 60.8);//身上有60.8元现金
        //左腿定义
        Map<String, Object> leg1 = new HashMap<>();
        leg1.put("leftOrRight", "left");//左腿
        leg1.put("length", 20.3);//腿长多少
        leg1.put("hair", 3000);//有多少腿毛
        //右腿定义
        Map<String, Object> leg2 = new HashMap<>();
        leg2.put("leftOrRight", "right");//右腿
        leg2.put("length", 20.3);//腿长多少
        leg2.put("hair", 3050);//有多少腿毛
        //给他两条腿
        List<Map<String, Object>> legs = new ArrayList<>();
        legs.add(leg1);
        legs.add(leg2);
        person.put("leg", legs);
        //让这个人变成一个Context，以便Jexl认识他
        JexlContext context = new MapContext(person);
        JexlEngine engine = new JexlEngine(); //定义引擎，1.1与2.1的用法不同，1.1使用的是工厂
        //看看这个人是否年龄在30岁以上，并且身上有超过100元现金
        boolean yes = (Boolean) engine.createExpression("age>30&&cash>100").evaluate(context);
        System.out.println("年龄在30岁以上，并且身上有超过100元现金?" + yes);//他没有
        //看看这个人是否左腿上有超过2500根汗毛
        yes = (Boolean) engine.createExpression("leg[0].hair>2500").evaluate(context);
        System.out.println("左腿上有超过2500根汗毛?" + yes);//是的，他有
    }
}
