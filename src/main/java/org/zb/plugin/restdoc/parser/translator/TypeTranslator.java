package org.zb.plugin.restdoc.parser.translator;

import java.util.HashSet;
import java.util.Set;

public class TypeTranslator {


    public static final String TYPE_LIST = "List";
    public static final String TYPE_MAP = "Map";
    public static final String TYPE_OBJ = "object";
    public static final String TYPE_VOID = "void";


    public static Set<String> charTypeList;
    public static Set<String> intTypeList;
    public static Set<String> doubleTypeList;
    public static Set<String> bigIntTypeList;
    public static Set<String> dateTypeList;
    public static Set<String> collectionTypeList;
    public static Set<String> mapTypeList;
    public static Set<String> boolTypeList;

    static {
        initCharTypeList();
        initDoubleTypeList();
        initIntTypeList();
        initBoolTypeList();
        initBigIntList();
        initDateTypeList();
        initCollectionTypeList();
        initMapTypeList();


    }

    public static void initCharTypeList() {
        charTypeList = new HashSet<>();
        charTypeList.add("java.lang.String");
    }

    public static void initDoubleTypeList() {
        doubleTypeList = new HashSet<>();
        doubleTypeList.add("double");
        doubleTypeList.add("float");
        doubleTypeList.add("java.lang.Double");
        doubleTypeList.add("java.lang.Float");
        doubleTypeList.add("java.math.BigDecimal");
    }

    public static void initIntTypeList() {
        intTypeList = new HashSet<>();
        intTypeList.add("int");
        intTypeList.add("short");
        intTypeList.add("byte");
        intTypeList.add("java.lang.Integer");
        intTypeList.add("java.lang.Byte");
        intTypeList.add("java.lang.Short");
    }

    public static void initBoolTypeList() {
        boolTypeList = new HashSet<>();
        boolTypeList.add("java.lang.Boolean");
        boolTypeList.add("boolean");
    }

    public static void initBigIntList() {
        bigIntTypeList = new HashSet<>();
        bigIntTypeList.add("long");
        bigIntTypeList.add("java.lang.Long");
    }

    public static void initDateTypeList() {
        dateTypeList = new HashSet<>();
        dateTypeList.add("java.util.Date");
        dateTypeList.add("java.sql.Timestamp");
        dateTypeList.add("java.sql.Date");
    }

    public static void initCollectionTypeList() {
        collectionTypeList = new HashSet<>();
        collectionTypeList.add("java.util.List");
        collectionTypeList.add("java.util.ArrayList");
        collectionTypeList.add("java.util.concurrent.CopyOnWriteArrayList");
        collectionTypeList.add("java.util.Vector");
        collectionTypeList.add("java.util.Set");
        collectionTypeList.add("java.util.HashSet");
        collectionTypeList.add("java.util.concurrent.CopyOnWriteArraySet");
        collectionTypeList.add("java.util.TreeSet");
    }

    public static void initMapTypeList() {
        mapTypeList = new HashSet<>();
        mapTypeList.add("java.util.Map");
        mapTypeList.add("java.util.TreeMap");
        mapTypeList.add("java.util.HashMap");
        mapTypeList.add("java.util.LinkedHashMap");
    }

    public static String docTypeTranslate(String javaType) {

        if (bigIntTypeList.contains(javaType)) {
            return "long";
        }
        if (boolTypeList.contains(javaType)) {
            return "boolean";
        }
        if (charTypeList.contains(javaType)) {
            return "string";
        }
        if (intTypeList.contains(javaType)) {
            return "int";
        }
        if (doubleTypeList.contains(javaType)) {
            return "double";
        }
        if (dateTypeList.contains(javaType)) {
            return "date";
        }
        if (collectionTypeList.contains(javaType)) {
            return TypeTranslator.TYPE_LIST;
        }
        if (mapTypeList.contains(javaType)) {
            return TypeTranslator.TYPE_MAP;
        }
        if (TYPE_VOID.equals(javaType)) {
            return null;
        }
        return TypeTranslator.TYPE_OBJ;
    }


}
