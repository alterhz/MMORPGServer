package org.game.tool;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.game.core.message.Proto;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProtoAnnotationAdder {

    private static final JavaParser javaParser = new JavaParser();

    private static Map<String, Integer> protoIds;

    public static void main(String[] args) {
        // 打印当前路径
        System.out.println("当前路径: " + System.getProperty("user.dir"));

        // 如果包含一个参数，则作为ini路径，否则使用默认的ini路径
        String iniFilePath = args.length > 0 ? args[0] : "../../proto/json/ProtoIds.ini";
        String rootPath = args.length > 1 ? args[1] : "src/main/java/org/game/proto"; // 根目录

        protoIds = readProtoIdsFromIni(iniFilePath);

        addProtoAnnotationToAllJavaFiles(rootPath);
    }

    private static Map<String, Integer> readProtoIdsFromIni(String iniFilePath) {
        Map<String, Integer> protoIds = new HashMap<>();
        // 尝试在项目根目录下查找ProtoIds.ini文件
        Path iniPath = Paths.get(iniFilePath);
        if (!Files.exists(iniPath)) {
            throw new RuntimeException("ProtoIds.ini文件不存在:" + iniFilePath);
        }
        
        try (BufferedReader reader = Files.newBufferedReader(iniPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过空行和注释行
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }
                
                // 解析格式: CSLogin=10001
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0 && equalsIndex < line.length() - 1) {
                    String key = line.substring(0, equalsIndex).trim();
                    String valueStr = line.substring(equalsIndex + 1).trim();
                    try {
                        int value = Integer.parseInt(valueStr);
                        protoIds.put(key, value);
                    } catch (NumberFormatException e) {
                        System.err.println("无法解析协议ID: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("读取ProtoIds.ini文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        return protoIds;
    }

    public static void addProtoAnnotationToAllJavaFiles(String rootPath) {
        Path rootDir = Paths.get(rootPath);

        if (!Files.exists(rootDir)) {
            System.err.println("目录不存在: " + rootPath);
            return;
        }

        try {
            Files.walk(rootDir)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(ProtoAnnotationAdder::processJavaFile);

            System.out.println("处理完成！");

        } catch (IOException e) {
            System.err.println("遍历目录时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processJavaFile(Path javaFilePath) {
        try {
            // 解析Java文件
            CompilationUnit cu = javaParser.parse(javaFilePath.toFile()).getResult().get();

            // 检查是否已经有@Proto注解，避免重复添加
            if (hasProtoAnnotation(cu)) {
                System.out.println("跳过已包含@Proto注解的文件: " + javaFilePath);
                return;
            }

            // 获取类名
            Optional<String> classNameOpt = cu.getPrimaryTypeName();
            if (!classNameOpt.isPresent()) {
                System.out.println("无法获取类名: " + javaFilePath);
                return;
            }
            
            String className = classNameOpt.get();

            // 只有在protoIds中存在该类名时才处理
            if (!protoIds.containsKey(className)) {
                System.err.println("跳过不存在ProtoIds.ini文件中的类: " + javaFilePath + " (类名: " + className + ")");
                return;
            }
            
            // 添加Proto注解的导入语句
            cu.addImport(Proto.class);
            
            // 创建@Proto注解
            int protoId = protoIds.get(className);
            AnnotationExpr protoAnnotation = createProtoAnnotation(protoId);

            // 为所有类添加注解
            new ClassAnnotationVisitor().visit(cu, protoAnnotation);

            // 保存修改后的文件
            Files.write(javaFilePath, cu.toString().getBytes());
            System.out.println("已为文件添加@Proto注解: " + javaFilePath + " (ID: " + protoId + ")");

        } catch (Exception e) {
            System.err.println("处理文件时出错: " + javaFilePath + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean hasProtoAnnotation(CompilationUnit cu) {
        // 检查编译单元是否有@Proto注解
        return cu.getTypes().stream()
                .anyMatch(type -> type.getAnnotationByName("Proto").isPresent());
    }

    private static AnnotationExpr createProtoAnnotation(int protoId) {
        // 创建 @Proto 注解
        // 如果是标记注解：@Proto
        // return new MarkerAnnotationExpr(new Name("Proto"));

        // 如果是普通注解，可以带参数：@Proto(value = "someValue")
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        annotation.setName(new Name(Proto.class.getSimpleName()));
        annotation.addPair("value", String.valueOf(protoId));

        return annotation;
    }

    /**
     * 访问者模式：为类添加注解
     */
    static class ClassAnnotationVisitor extends VoidVisitorAdapter<AnnotationExpr> {
        @Override
        public void visit(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration n, AnnotationExpr protoAnnotation) {
            super.visit(n, protoAnnotation);
            // 为类添加注解
            if (!n.getAnnotationByName("Proto").isPresent()) {
                n.addAnnotation(protoAnnotation.clone());
            }
        }

        @Override
        public void visit(com.github.javaparser.ast.body.EnumDeclaration n, AnnotationExpr protoAnnotation) {
            super.visit(n, protoAnnotation);
            // 为枚举添加注解
            if (!n.getAnnotationByName("Proto").isPresent()) {
                n.addAnnotation(protoAnnotation.clone());
            }
        }

        @Override
        public void visit(com.github.javaparser.ast.body.RecordDeclaration n, AnnotationExpr protoAnnotation) {
            super.visit(n, protoAnnotation);
            // 为记录类添加注解（Java 14+）
            if (!n.getAnnotationByName("Proto").isPresent()) {
                n.addAnnotation(protoAnnotation.clone());
            }
        }
    }
}