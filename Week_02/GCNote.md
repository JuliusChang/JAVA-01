# <center>GC总结笔记</center>

```java
//使用程序

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String[] args) {
        
        long startMillis = System.currentTimeMillis();
        
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        
        while (System.currentTimeMillis() < endMillis) {
            
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println(counter.longValue());
    }


    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```

## 串行GC

运行参数如下：

```java
java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
```

打印GC日志如下(由于不便于在MD文件中展示，已将时间信息省略)：

```java
//第一次young区满了，使用20ms将young区从88%清理到11%，其中有28646k转移到了old区，占old区8%大小
[GC (Allocation Failure) [DefNew: 139092K->17471K(157248K), 0.0191921 secs] 139092K->46117K(506816K), 0.0195841 secs] [Times: user=0.00 sys=0.01, real=0.02 secs]

//过了100毫秒后，第二次youngGC，old区垃圾占比19%，耗时20ms
[GC (Allocation Failure) [DefNew: 157052K->17470K(157248K), 0.0227403 secs] 185698K->84384K(506816K), 0.0230645 secs] [Times: user=0.02 sys=0.02, real=0.02 secs]

//40ms后，第三次youngGC，old区垃圾占比32%，耗时20ms
[GC (Allocation Failure) [DefNew: 157246K->17471K(157248K), 0.0208245 secs] 224160K->130449K(506816K), 0.0211713 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [DefNew: 157247K->17471K(157248K), 0.0209371 secs] 270225K->174946K(506816K), 0.0212092 secs] [Times: user=0.02 sys=0.02, real=0.02 secs]
[GC (Allocation Failure) [DefNew: 157209K->17471K(157248K), 0.0214523 secs] 314683K->220364K(506816K), 0.0217463 secs] [Times: user=0.01 sys=0.02, real=0.02 secs]
[GC (Allocation Failure) [DefNew: 157185K->17472K(157248K), 0.0239942 secs] 360079K->271749K(506816K), 0.0242594 secs] [Times: user=0.00 sys=0.02, real=0.02 secs]

//youngGC过后，old区垃圾占比84.9%，占比非常高，将要进行FULLGC
[GC (Allocation Failure) [DefNew: 157248K->17471K(157248K), 0.0204486 secs] 411525K->314320K(506816K), 0.0207218 secs] [Times: user=0.02 sys=0.01, real=0.02 secs]

//这次信息可以看出younggc过后，占比没有变，再看一下后面old区已经满了，无法将大年龄对象转移到old区，于是引起oldGC，但GC的效果也不明显，只减少了30m左右，但堆占有量减少了193631，推出oldGC后随后进行了youngGC，一部分进入老年代，所以oldGC不明显。
[GC (Allocation Failure) [DefNew: 157247K->157247K(157248K), 0.0003071 secs][Tenured: 296848K->260464K(349568K), 0.0290074 secs] 454096K->260464K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0299123 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [DefNew: 139776K->17471K(157248K), 0.0068594 secs] 400240K->304973K(506816K), 0.0071296 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [DefNew: 157247K->17469K(157248K), 0.0204532 secs] 444749K->353607K(506816K), 0.0207329 secs] [Times: user=0.00 sys=0.02, real=0.02 secs]
[GC (Allocation Failure) [DefNew: 157078K->157078K(157248K), 0.0002025 secs][Tenured: 336137K->312737K(349568K), 0.0322601 secs] 493215K->312737K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0330545 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [DefNew: 139776K->139776K(157248K), 0.0002054 secs][Tenured: 312737K->323377K(349568K), 0.0329602 secs] 452513K->323377K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0336327 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [DefNew: 139776K->139776K(157248K), 0.0002043 secs][Tenured: 323377K->319163K(349568K), 0.0352183 secs] 463153K->319163K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0358753 secs] [Times: user=0.05 sys=0.00, real=0.04 secs]
[GC (Allocation Failure) [DefNew: 139776K->139776K(157248K), 0.0002116 secs][Tenured: 319163K->339154K(349568K), 0.0224695 secs] 458939K->339154K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0232138 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
[GC (Allocation Failure) [DefNew: 139468K->139468K(157248K), 0.0002051 secs][Tenured: 339154K->345475K(349568K), 0.0311865 secs] 478623K->345475K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0318593 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [DefNew: 139776K->139776K(157248K), 0.0002091 secs][Tenured: 345475K->349539K(349568K), 0.0339021 secs] 485251K->353013K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0346812 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
//当对象一直无法释放掉就开始不停的fullGC了，每次50ms，
[Full GC (Allocation Failure) [Tenured: 349539K->339010K(349568K), 0.0376265 secs] 506708K->339010K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0381934 secs] [Times: user=0.05 sys=0.00, real=0.04 secs]
[GC (Allocation Failure) [DefNew: 139776K->139776K(157248K), 0.0001697 secs][Tenured: 339010K->349484K(349568K), 0.0257853 secs] 478786K->355695K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0265611 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
[Full GC (Allocation Failure) [Tenured: 349556K->349537K(349568K), 0.0326807 secs] 506783K->360051K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0331084 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
[Full GC (Allocation Failure) [Tenured: 349537K->349265K(349568K), 0.0350072 secs] 506784K->363876K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0356692 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
[Full GC (Allocation Failure) [Tenured: 349553K->349235K(349568K), 0.0389234 secs] 506654K->349877K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0393308 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
[Full GC (Allocation Failure) [Tenured: 349235K->348990K(349568K), 0.0290198 secs] 506267K->372373K(506816K), [Metaspace: 2709K->2709K(1056768K)], 0.0293943 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
12236
Heap
 def new generation   total 157248K, used 139723K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  99% used [0x00000000e0000000, 0x00000000e8872c08, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
  to   space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
 tenured generation   total 349568K, used 348990K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
   the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000fff6f870, 0x00000000fff6fa00, 0x0000000100000000)
 Metaspace       used 2715K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 292K, capacity 386K, committed 512K, reserved 1048576K

```

总结：由于串行化GC的youngGC和oldGC都产生会产生STW这对实时性要求好的系统是致命的，同时因为是串行化，导致系统吞吐量很低。

## 并行GC

```java
java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails GCLogAnalysis
```

打印日志如下：

```java

[GC (Allocation Failure) [PSYoungGen: 131079K->21489K(153088K)] 131079K->40901K(502784K), 0.0074928 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 153073K->21503K(153088K)] 172485K->79040K(502784K), 0.0110758 secs] [Times: user=0.00 sys=0.08, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 153064K->21500K(153088K)] 210600K->119532K(502784K), 0.0097506 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 152553K->21494K(153088K)] 250585K->157189K(502784K), 0.0091924 secs] [Times: user=0.05 sys=0.03, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 152761K->21498K(153088K)] 288456K->202896K(502784K), 0.0133053 secs] [Times: user=0.02 sys=0.06, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 153082K->21495K(80384K)] 334480K->245717K(430080K), 0.0096715 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 80375K->36492K(116736K)] 304597K->265639K(466432K), 0.0050526 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 95372K->47421K(116736K)] 324519K->281236K(466432K), 0.0062802 secs] [Times: user=0.08 sys=0.02, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 106301K->54597K(116736K)] 340116K->297252K(466432K), 0.0076786 secs] [Times: user=0.09 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 113410K->36655K(116736K)] 356065K->312708K(466432K), 0.0092283 secs] [Times: user=0.08 sys=0.02, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 95535K->18437K(116736K)] 371588K->330012K(466432K), 0.0082208 secs] [Times: user=0.03 sys=0.06, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 18437K->0K(116736K)] [ParOldGen: 311575K->230369K(349696K)] 330012K->230369K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0270536 secs] [Times: user=0.08 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58525K->19768K(116736K)] 288895K->250137K(466432K), 0.0029323 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 78630K->17102K(116736K)] 308999K->266527K(466432K), 0.0042060 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 75982K->21038K(116736K)] 325407K->286641K(466432K), 0.0041207 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 79750K->17613K(116736K)] 345353K->304007K(466432K), 0.0046252 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 76408K->21344K(116736K)] 362802K->324978K(466432K), 0.0042576 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 21344K->0K(116736K)] [ParOldGen: 303634K->260332K(349696K)] 324978K->260332K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0266640 secs] [Times: user=0.19 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58880K->21188K(116736K)] 319212K->281520K(466432K), 0.0029754 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 80068K->21986K(116736K)] 340400K->301793K(466432K), 0.0046040 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 80866K->20151K(116736K)] 360673K->321330K(466432K), 0.0045744 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 79031K->20904K(116736K)] 380210K->341470K(466432K), 0.0055119 secs] [Times: user=0.06 sys=0.02, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 20904K->0K(116736K)] [ParOldGen: 320566K->285020K(349696K)] 341470K->285020K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0279855 secs] [Times: user=0.08 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58306K->21402K(116736K)] 343326K->306422K(466432K), 0.0026341 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 80282K->23238K(116736K)] 365302K->327887K(466432K), 0.0049221 secs] [Times: user=0.09 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 82118K->17904K(116736K)] 386767K->344709K(466432K), 0.0045689 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 17904K->0K(116736K)] [ParOldGen: 326804K->297533K(349696K)] 344709K->297533K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0293644 secs] [Times: user=0.11 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58405K->17511K(116736K)] 355939K->315045K(466432K), 0.0027117 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 76382K->19825K(116736K)] 373916K->333624K(466432K), 0.0040956 secs] [Times: user=0.08 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 78448K->21658K(116736K)] 392248K->353511K(466432K), 0.0053509 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 21658K->0K(116736K)] [ParOldGen: 331853K->303320K(349696K)] 353511K->303320K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0308834 secs] [Times: user=0.17 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58880K->24197K(116736K)] 362200K->327517K(466432K), 0.0031199 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 83077K->41995K(115712K)] 386397K->345315K(465408K), 0.0046807 secs] [Times: user=0.08 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 100588K->19786K(116736K)] 403908K->363803K(466432K), 0.0073306 secs] [Times: user=0.06 sys=0.01, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 19786K->0K(116736K)] [ParOldGen: 344017K->312075K(349696K)] 363803K->312075K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0314689 secs] [Times: user=0.19 sys=0.00, real=0.03 secs]
[GC (Allocation Failure) [PSYoungGen: 58879K->21299K(118784K)] 370955K->333374K(468480K), 0.0026493 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 82227K->38297K(116736K)] 394302K->350372K(466432K), 0.0048731 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 99122K->57291K(116224K)] 411197K->371384K(465920K), 0.0067273 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 115798K->42569K(116736K)] 429891K->389513K(466432K), 0.0080074 secs] [Times: user=0.08 sys=0.00, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 42569K->0K(116736K)] [ParOldGen: 346943K->329757K(349696K)] 389513K->329757K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0336084 secs] [Times: user=0.16 sys=0.02, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58691K->0K(116736K)] [ParOldGen: 329757K->332304K(349696K)] 388448K->332304K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0323722 secs] [Times: user=0.20 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58845K->0K(116736K)] [ParOldGen: 332304K->337408K(349696K)] 391149K->337408K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0326524 secs] [Times: user=0.19 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58628K->0K(116736K)] [ParOldGen: 337408K->335487K(349696K)] 396037K->335487K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0329617 secs] [Times: user=0.13 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58880K->0K(116736K)] [ParOldGen: 335487K->342998K(349696K)] 394367K->342998K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0325987 secs] [Times: user=0.19 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58593K->0K(116736K)] [ParOldGen: 342998K->345461K(349696K)] 401592K->345461K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0337576 secs] [Times: user=0.23 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58880K->0K(116736K)] [ParOldGen: 345461K->345425K(349696K)] 404341K->345425K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0347579 secs] [Times: user=0.17 sys=0.00, real=0.03 secs]
[Full GC (Ergonomics) [PSYoungGen: 58880K->453K(116736K)] [ParOldGen: 345425K->349354K(349696K)] 404305K->349807K(466432K), [Metaspace: 2709K->2709K(1056768K)], 0.0299880 secs] [Times: user=0.16 sys=0.00, real=0.03 secs]
10600
Heap
 PSYoungGen      total 116736K, used 3082K [0x00000000f5580000, 0x0000000100000000, 0x0000000100000000)
  eden space 58880K, 5% used [0x00000000f5580000,0x00000000f5882980,0x00000000f8f00000)
  from space 57856K, 0% used [0x00000000f8f00000,0x00000000f8f00000,0x00000000fc780000)
  to   space 57856K, 0% used [0x00000000fc780000,0x00000000fc780000,0x0000000100000000)
 ParOldGen       total 349696K, used 349354K [0x00000000e0000000, 0x00000000f5580000, 0x00000000f5580000)
  object space 349696K, 99% used [0x00000000e0000000,0x00000000f552a848,0x00000000f5580000)
 Metaspace       used 2715K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 292K, capacity 386K, committed 512K, reserved 1048576K
```

可以看到由于parallelGC在标记和复制整理阶段都会使用线程并行处理，可以增加系统的吞吐量。JDK8默认为parallelGC。

## CMS GC

```java
java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails GCLogAnalysis
```

部分cmsGC日志如下：

```java
//标记所有跟对象产生STW，但是非常的短，0.7ms
[GC (CMS Initial Mark) [1 CMS-initial-mark: 209425K(349568K)] 229799K(506816K), 0.0007962 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
//并发标记
[CMS-concurrent-mark-start]
[CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
//并发预清理
[CMS-concurrent-preclean-start]
[CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[CMS-concurrent-abortable-preclean-start]
[GC (Allocation Failure) [ParNew: 157246K->17471K(157248K), 0.0193568 secs] 366672K->270555K(506816K), 0.0197004 secs] [Times: user=0.06 sys=0.03, real=0.02 secs]
[GC (Allocation Failure) [ParNew: 157247K->17470K(157248K), 0.0195260 secs] 410331K->314152K(506816K), 0.0197514 secs] [Times: user=0.06 sys=0.03, real=0.02 secs]
[GC (Allocation Failure) [ParNew: 156941K->17471K(157248K), 0.0215919 secs] 453623K->361751K(506816K), 0.0217768 secs] [Times: user=0.08 sys=0.02, real=0.02 secs]
[CMS-concurrent-abortable-preclean: 0.002/0.100 secs] [Times: user=0.25 sys=0.08, real=0.10 secs]
//最终标记，产生stw
[GC (CMS Final Remark) [YG occupancy: 24605 K (157248 K)][Rescan (parallel) , 0.0002477 secs][weak refs processing, 0.0000459 secs][class unloading, 0.0004657 secs][scrub symbol table, 0.0002957 secs][scrub string table, 0.0001138 secs][1 CMS-remark: 344279K(349568K)] 368885K(506816K), 0.0020421 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
//并发清除
[CMS-concurrent-sweep-start]
[CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
//并发重置
[CMS-concurrent-reset-start]
[CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
```

可以看出cms是并发的，中间还穿插几次youngGC，由于cmsGC大部分时间都不会产生STW，所以很好的解决了系统GC停顿问题，但由于没有压缩老年代内存碎片，在某些情况可能造成不可预测的暂停时间问题。同时由于占用了一部分线程，会导致系统吞吐量降低。

## G1

G1可以与CMS都可以降低停顿时间，但不同的是G1还可以建立可预测的停顿时间模型，可以让使用者明确指定在一个长度为M毫秒的时间片段内，同时G1完全并行化减少fullGC的时间，进一步降低停顿时间。jdk9以后默认使用G1垃圾回收器。

## 总结：

- 考虑吞吐选择 ParallelGC
- 低延迟 CMSGC
- 均衡G1 GC
- 大内存推荐用G1 GC
- 超大内存使用ZGC
- 堆内存大会导致暂停时间过长吞吐量低，堆内存小会导致 OOM
- 并行GC线程数做好手动限制，cpu 8核以下使用和cpu核心数相同的，8核以上使用 n*5/8+3，n为核心数
- 建议设置一下meta区的大小，以便排查问题