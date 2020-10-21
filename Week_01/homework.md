# homework
2. 自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。

```
package homework2;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author eazonshaw
 * @date 2020/10/20  22:12
 *
 * 自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。
 */
public class HelloClassLoader extends ClassLoader{

    public static void main(String[] args){

        HelloClassLoader classLoader = new HelloClassLoader();
        try {
            Class<?> aClass = classLoader.findClass("Hello");
            Object obj = aClass.newInstance();
            Method method = aClass.getMethod("hello");
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
       byte[] classData = getClassData(name);
       return defineClass(name,classData,0,classData.length);
    }

    private byte[] getClassData(String name){
        byte[] bytes = null;
        try {
            bytes = covertClassBytes(IOUtils.toByteArray(this.getClass().getResourceAsStream("Hello.xlass")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private byte[] covertClassBytes(byte[] classBytes) {
        byte[] bytes = new byte[classBytes.length];
        byte tmp;
        for(int i = 0; i < classBytes.length; i++){
            tmp = classBytes[i];
            bytes[i] = (byte)(255-tmp);
        }
        return bytes;
    }
}
```

3. 画一张图，展示 Xmx、Xms、Xmn、Meta、DirectMemory、Xss 这些内存参数的关系。

![]()