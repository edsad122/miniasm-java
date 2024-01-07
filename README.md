release中已有编译好的jar，用法：
  1.请在jar文件路径下，创建bios的文件，将src\snippet中3个asm文件放进(如果没有asm文件，无法使用链接，也可以使用自己的bios文件但需要改成相应的名字，这3个文件分别用来：bios引导系统程序入口、interpret-entry中断程序的入口、interpret-handler中断程序处理)
  2.键入#java -jar miniasm-java.jar [输入asm文件路径] [输出文件路径] [-l]# -l 为可选选项，表示是否链接。
