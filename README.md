## 宏指令
支持push、pop、jg、jge、jl、jle、move指令。
## 链接功能

minisys-asm 兼具链接功能，可以完成内存的布局、地址的重定位。

## 使用
release中已有编译好的jar，用法：

1. 请在jar文件路径下，创建bios的文件，将src\snippet中3个asm文件放进bios中
    - 如果没有asm文件，无法使用链接，既无法加上 "-l" 参数。
    - 可以使用自己的bios文件，但需要改成相应的名字。
    - src\snippet中3个文件分别用来：bios引导系统程序入口、interpret-entry中断程序的入口、interpret-handler中断程序处理 。

2. jar的项目结构如下：
```
jar文件路径
│  miniasm-java.jar
│
└─bios
        minisys-bios.asm
        minisys-interrupt-entry.asm
        minisys-interrupt-handler.asm
```
3. 指令格式如下所示，其中 "-l" 为可选选项，表示是否链接。
```bash
$ java -jar minisys-java.jar <in_file> <out_dir> -l
```

### 内存布局

Minisys 体系使用哈佛结构，指令 MEM 有 64 KB，按字节编址。因此，其地址范围为 0x00000000 ~ 0x0000FFFF。指令 MEM 布局如下：

| 地址                    | 作用                                                         |
| ----------------------- | ------------------------------------------------------------ |
| 0x00000000 ~ 0x00000499 | BIOS 区域。大小为 500 H = 1280 D Byte，最多存放 1280 / 4 = 320 条指令。 |
| 0x00000500 ~ 0x00005499 | 用户程序区域。大小为 5000 H = 20480 D Byte，最多存放 20480 / 4 =  5120 条指令。 |
| 0x00005500 ~ 0x0000EFFF | 空。                                                         |
| 0x0000F000 ~ 0x0000F499 | 中断处理程序入口。大小为 500 H = 1280 D Byte，最多存放 1280 / 4 = 320 条指令。 |
| 0x0000F500 ~ 0x0000FFFF | 中断处理程序。大小为 B00 H = 2816 D Byte，最多存放 2816 / 4 = 704 条指令。 |


