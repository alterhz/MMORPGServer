using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Editing;
using static Microsoft.CodeAnalysis.CSharp.SyntaxFactory;

namespace GenClientProto
{
    class ProtoAttributeAdder
    {
        private static Dictionary<string, int> protoIds;

        static void Main(string[] args)
        {
            Console.WriteLine($"当前路径: {Directory.GetCurrentDirectory()}");

            // 参数：0: ini路径，1: 根目录
            string iniFilePath = args.Length > 0 ? args[0] : @"D:\MyZiegler\ZRepo\github\MMORPGServer\proto\json\ProtoIds.ini";
            string rootPath = args.Length > 1 ? args[1] : @"D:\MyZiegler\ZRepo\github\MMORPGServer\client\zgame\Assets\Scripts\Proto\";

            protoIds = ReadProtoIdsFromIni(iniFilePath);

            if (!Directory.Exists(rootPath))
            {
                Console.WriteLine($"目录不存在: {rootPath}");
                return;
            }

            AddProtoAttributeToAllCsFiles(rootPath);

            Console.WriteLine("处理完成！");
        }

        private static Dictionary<string, int> ReadProtoIdsFromIni(string iniFilePath)
        {
            var protoIds = new Dictionary<string, int>();
            var iniPath = Path.GetFullPath(iniFilePath);

            if (!File.Exists(iniPath))
            {
                throw new FileNotFoundException("ProtoIds.ini 文件不存在", iniPath);
            }

            foreach (var line in File.ReadAllLines(iniPath))
            {
                var trimmed = line.Trim();
                if (string.IsNullOrEmpty(trimmed) || trimmed.StartsWith("#") || trimmed.StartsWith(";"))
                    continue;

                var parts = trimmed.Split('=', 2);
                if (parts.Length != 2) continue;

                var key = parts[0].Trim().ToLower();
                if (int.TryParse(parts[1].Trim(), out int value))
                {
                    protoIds[key] = value;
                    Console.WriteLine($"已读取协议ID: {key} = {value}");
                }
                else
                {
                    Console.WriteLine($"无法解析协议ID: {line}");
                }
            }

            return protoIds;
        }

        private static void AddProtoAttributeToAllCsFiles(string rootPath)
        {
            var csFiles = Directory.EnumerateFiles(rootPath, "*.cs", SearchOption.AllDirectories);

            foreach (var filePath in csFiles)
            {
                ProcessCsFile(filePath);
            }
        }

        private static void ProcessCsFile(string filePath)
        {
            try
            {
                var code = File.ReadAllText(filePath);
                var tree = CSharpSyntaxTree.ParseText(code);
                var root = tree.GetCompilationUnitRoot();

                // 检查是否有 Proto 特性导入
                var hasProtoImport = root.Usings.Any(u => u.Name.ToString() == "org.game.core.message");

                bool modified = false;
                CompilationUnitSyntax newRoot = root;

                // 遍历所有类、接口、枚举、记录
                var members = root.DescendantNodes().OfType<TypeDeclarationSyntax>();
                foreach (var member in members)
                {
                    var typeName = member.Identifier.Text;

                    // 跳过已有 [Proto(...)] 特性的类型
                    if (HasProtoAttribute(member))
                    {
                        Console.WriteLine($"[ERROR]跳过已包含 [Proto] 特性的类: {filePath} ({typeName})");
                        continue;
                    }

                    // 只处理在 protoIds 中存在的类名
                    if (!protoIds.ContainsKey(typeName.ToLower()))
                    {
                        Console.WriteLine($"[ERROR]跳过不在 ProtoIds.ini 中的类: {filePath} ({typeName})");
                        continue;
                    }

                    var protoId = protoIds[typeName.ToLower()];

                    // 创建 [Proto(123)]
                    var protoAttr = Attribute(
                        IdentifierName("Proto"),
                        AttributeArgumentList(
                            SingletonSeparatedList(
                                AttributeArgument(
                                    LiteralExpression(
                                        SyntaxKind.NumericLiteralExpression,
                                        Literal(protoId)
                                    )
                                )
                            )
                        )
                    );

                    // 直接修改节点而不是使用 SyntaxEditor
                    var updatedMember = member.WithAttributeLists(
                        member.AttributeLists.Insert(0,
                            AttributeList(SingletonSeparatedList(protoAttr))
                        )
                    );

                    newRoot = newRoot.ReplaceNode(member, updatedMember);
                    modified = true;
                    Console.WriteLine($"已为 {filePath} 添加 [Proto] 特性: {typeName} (ID: {protoId})");
                }

                // 如果有修改，写回文件
                if (modified)
                {
                    // 添加 using org.game.core.message; 如果没有
                    if (!hasProtoImport)
                    {
                        var newUsing = UsingDirective(ParseName("org.game.core.message"));
                        //newRoot = newRoot.AddUsings(newUsing);
                    }

                    // 格式化代码以确保正确的缩进和对齐
                    var formattedRoot = newRoot.NormalizeWhitespace();
                    File.WriteAllText(filePath, formattedRoot.ToFullString());
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"处理文件出错: {filePath}, 错误: {ex.Message}");
                Console.WriteLine(ex.StackTrace);
            }
        }

        private static bool HasProtoAttribute(TypeDeclarationSyntax typeDecl)
        {
            foreach (var attrList in typeDecl.AttributeLists)
            {
                foreach (var attr in attrList.Attributes)
                {
                    if (attr.Name is IdentifierNameSyntax ident && ident.Identifier.Text == "Proto")
                        return true;
                    if (attr.Name is QualifiedNameSyntax qname)
                    {
                        if (qname.Right.Identifier.Text == "Proto") return true;
                    }
                }
            }
            return false;
        }
    }
}