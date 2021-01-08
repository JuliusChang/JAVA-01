import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            Class<?> aClass =new HelloClassLoader().findClass("Hello");
            Object obj = aClass.newInstance();
            aClass.getMethod("hello").invoke(obj);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = dataUtils();
        return defineClass(name,bytes,0,bytes.length);
    }

    private byte[] dataUtils(){
        String localFile = "Hello.xlass";
        byte[] data = null;
        try {
            data = loadFile(localFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < data.length;i++){
            data[i] = (byte)(255 - data[i]);
        }
        return data;
    }

    private byte[] loadFile(String fileName) throws FileNotFoundException {
        FileInputStream is = null;
        File file = new File(fileName);

        is = new FileInputStream(file);
        byte[] buf = new byte[(int)file.length()];
        try {
            is.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buf;

    }
}
