package com.example.newsapp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SerializeUtils {
    public static String charsetName = StandardCharsets.UTF_8.toString();

    @Nullable
    public static String serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            String string = byteArrayOutputStream.toString(charsetName);
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Object serializeToObject(@NotNull String str, @NotNull Class clazz) {
        Object object = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(charsetName)); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Serializable> void write(T t, String outPath) throws Exception {
        ObjectOutputStream oos = null;
        try {
            File file = new File(outPath);
            if (!Objects.requireNonNull(file.getParentFile()).exists()) {
                file.getParentFile().mkdirs();
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(t);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    /**
     * 把文件转化成序列化的类
     * @param path
     * @return
     * @throws Exception
     */
    public static Serializable read(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object object = ois.readObject();
            if (object != null) {
                return (Serializable) object;
            }
        }
        return null;
    }
}
