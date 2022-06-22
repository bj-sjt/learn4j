package com.itao.jdk;

import java.util.ArrayList;
import java.util.List;

/**
 * all	                      to suppress all warnings <br/>
 * boxing 	                  to suppress warnings relative to boxing/unboxing operations <br/>
 * cast	                      to suppress warnings relative to cast operations <br/>
 * dep-ann	                  to suppress warnings relative to deprecated annotation <br/>
 * deprecation	              to suppress warnings relative to deprecation <br/>
 * fallthrough	              to suppress warnings relative to missing breaks in switch statements <br/>
 * finally 	                  to suppress warnings relative to finally block that don’t return <br/>
 * hiding	                  to suppress warnings relative to locals that hide variable <br/>
 * incomplete-switch	      to suppress warnings relative to missing entries in a switch statement (enum case) <br/>
 * nls	                      to suppress warnings relative to non-nls string literals <br/>
 * null	                      to suppress warnings relative to null analysis <br/>
 * rawtypes	                  to suppress warnings relative to un-specific types when using generics on class params <br/>
 * restriction	              to suppress warnings relative to usage of discouraged or forbidden references <br/>
 * serial	                  to suppress warnings relative to missing serialVersionUID field for a serializable class <br/>
 * static-access	          to suppress warnings relative to incorrect static access <br/>
 * synthetic-access 	      to suppress warnings relative to unoptimized access from inner classes <br/>
 * unchecked	              to suppress warnings relative to unchecked operations <br/>
 * unqualified-field-access	  to suppress warnings relative to field access unqualified <br/>
 * unused	                  to suppress warnings relative to unused code <br/>
 * @see SuppressWarnings
 */
public class TestSuppressWarnings {

    @SuppressWarnings("uncheck")
    public static void main(String[] args) {
        @SuppressWarnings("unused") // 抑制未使用变量的警告
        String unuse = "unused";

        @SuppressWarnings("rawtypes") // 抑制类型
        List items = new ArrayList();
    }
}
