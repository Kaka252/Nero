package leo.nero.com.leo.sp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import leo.nero.com.leo.App;

/**
 * Created by zhouyou on 2015/7/17.
 */
public class Nero {

    /**
     * 声明实例：该实例在App启动之后声明，并在Application的生命周期里保存存活状态
     */
    private static final Nero INSTANCE = new Nero();

    private static String SP_NAME = App.getInstance().getPackageName();

    private Context mContext;

    /**
     * 该标记位用来确保在访问实例的时候，该实例已经初始化
     */
    private boolean wasInitialized = false;

    /**
     * in-memory data | 在内存中记录偏好数据
     */
    private Map<String, Object> mData;

    public Nero() {

    }

    private void initContext(Context context) {
        mContext = context.getApplicationContext();
        SharedPreferences sp = getSharedPreferences();
        mData = new ConcurrentHashMap<>();
        mData.putAll(sp.getAll());
        wasInitialized = true;
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 核心调用方法，在App的onCreate()的方法中进行注册
     * 单例模式
     */
    public static synchronized Nero init(Context context) {
        if (context == null) {
            throw new RuntimeException("You have no context and you need to initialize it first.");
        }

        if (!INSTANCE.wasInitialized) {
            INSTANCE.initContext(context);
        }

        return INSTANCE;
    }

    private static Nero getInstance() {
        if (!INSTANCE.wasInitialized) {
            throw new RuntimeException("You must call Nero.init() before using this.");
        }
        return INSTANCE;
    }

    /**
     * *********************************** 保存|获取数据 ******************************************
     */

    /**
     * 写入数据
     * @param key
     * @param value
     * @return
     */
    private boolean saveIn(String key, Object value) {
        boolean isSuccess = false;

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        boolean isPut = true;
        if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            isPut = false;
        }

        if (isPut) {
            isSuccess = editor.commit();
            mData.put(key, value);
        }

        return isSuccess;
    }

    /**
     * 从内存中读取偏好数据
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T get(String key, Class<T> clazz) {
        if (mData == null) return null;
        Object value = mData.get(key);
        T obj = null;
        if (clazz.isInstance(value)) {
            obj = clazz.cast(value);
        }
        return obj;
    }

    /**
     * *********************************** 以下为put、get ******************************************
     */

    public static void putFloat(String key, float value) {
        getInstance().saveIn(key, value);
    }

    public static void putLong(String key, long value) {
        getInstance().saveIn(key, value);
    }

    public static void putString(String key, String value) {
        getInstance().saveIn(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        getInstance().saveIn(key, value);
    }

    public static void putInt(String key, int value) {
        getInstance().saveIn(key, value);
    }

    public static float getFloat(String key, float defValue) {
        Float value = getInstance().get(key, Float.class);
        if (value != null) {
            return value;
        }
        return defValue;
    }

    public static int getInt(String key, int defValue) {
        Integer value = getInstance().get(key, Integer.class);
        if (value != null) {
            return value;
        }
        return defValue;
    }

    public static long getLong(String key, long defValue) {
        Long value = getInstance().get(key, Long.class);
        if (value != null) {
            return value;
        }
        return defValue;
    }

    public static String getString(String key, String defValue) {
        String value = getInstance().get(key, String.class);
        if (value != null) {
            return value;
        }
        return defValue;
    }

    public static boolean getBoolean(String key, boolean defValue) {
        Boolean value = getInstance().get(key, Boolean.class);
        if (value != null) {
            return value;
        }
        return defValue;
    }

    /**
     * ******************************* 额外判断key值是否存在的方法 ********************************
     */
    public static boolean containsKey(String key) {
        return getInstance().mData.containsKey(key);
    }

    /**
     * ******************************* 删除偏好 *****************************************************
     */
    public static void removeAt(String key) {
        if (getInstance().mData == null) return;
        getInstance().mData.remove(key);
        SharedPreferences.Editor editor = getInstance().getSharedPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearAll() {
        if (getInstance().mData == null) return;
        getInstance().mData.clear();
        SharedPreferences.Editor editor = getInstance().getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }


}