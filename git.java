import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * 除了基本命令外还额外实现了
 * git reflog
 * git status
 * git diff
 * git find
 * git branch
 * git rm -branch
 */
public class git {
    //首先将本次项目的所有文件路径都表示出来
    static String userPath = System.getProperty("user.dir");
    //gitPath表示.git文件夹的路径，它在当前工作目录下
    static String gitPath = userPath+ File.separator+".git";
    //objPath表示在.git文件夹中的objects文件夹的路径
    static String objPath = gitPath+File.separator+"objects";
    //indexPath表示.git文件夹中的index文件的路径
    static String indexPath = gitPath+File.separator+"index.txt";
    //headPath表示.git文件夹中的head文件的路径
    static String headPath = gitPath+File.separator+"head.txt";
    //headNPath表示.git文件夹中的headN文件的路径
    static String headNPath = gitPath+File.separator+"headN.txt";
    //headsPath表示.git文件夹中的heads文件夹的路径
    static String headsPath = gitPath+File.separator+"heads";
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("请输入您的命令");
        } else if (args[0].equals("init")) {
            //保持代码健壮性，保证只有git init才是合法输入
            if(args.length>1){
                System.out.println("您的输入不合法，请重新输入");
            }else {
                System.out.println("执行init命令");
                init();
            }
        } else if (args[0].equals("add")) {
            //保持代码健壮性，保证只有git add . 和git add 文件才是合法输入
            if(args.length != 2){
                System.out.println("您的输入不合法，请重新输入");
            }else if(args[1].equals(".")){
                addAll();
                System.out.println("执行add命令，add全部文件");
            }else{
                add(args[1]);
                System.out.println("执行add命令，add文件"+args[1]);
            }
        } else if (args[0].equals("commit")) {
            //保持代码健壮性，保证只有git commit -m "message"才是合法输入
            if(args.length !=3){
                System.out.println("您的输入不合法，请重新输入");
            } else if (!args[1].equals("-m")) {
                System.out.println("您的输入不合法，请重新输入");
            } else {
                commit(args[2]);
                System.out.println("执行commit命令");
            }
        } else if (args[0].equals("rm")) {
            //保持代码健壮性，保证只有git rm 文件名 还有git rm --cached 文件名，
            // git rm --branch name才是合法输入
            if(args.length==1||args.length>3){
                System.out.println("不合法的输入");
            }else if(args.length>2&&args[1].equals("--cached")){
                removeIndex(args[2]);
                System.out.println("成功执行rm --cached命令");
            }else if(args.length>2&&args[1].equals("--branch")){
                removeB(args[2]);
                System.out.println("成功执行rm --branch命令");
            }else{
                removeAll(args[1]);
                System.out.println("成功执行rm命令");
            }
        } else if(args[0].equals("log")){
            //保持代码健壮性，保证只有git log才是合法输入
            if(args.length>1){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                System.out.println("执行log操作");
                log();
            }
        } else if(args[0].equals("reset")){
            //保持代码健壮性，保证只有git reset commitId或
            //git reset --soft/--mixed/--hard id才是合法输入
            //即参数长度只能为2和3
            if(args.length==1||args.length>3){
                System.out.println("不合法的输入");
            }else if(args[1].equals("--soft")){
                System.out.println("执行reset --soft操作");
                resetSoft(args[2]);
            } else if (args[1].equals("--hard")) {
                System.out.println("执行reset --hard操作");
                resetHard(args[2],headPath,indexPath,userPath);
            } else if(args[1].equals("--mixed")||args.length==2){
                //默认模式，mixed
                System.out.println("执行reset --mixed操作");
                if(args[1].equals("--mixed")){
                    resetMixed(args[2]);
                }else{
                    resetMixed(args[1]);
                }
            }else{
                System.out.println("您的输入不合法，请重新输入");
            }
        } else if(args[0].equals("pull")){
            //保持代码健壮性，保证只有git pull是合法操作
            if (args.length!=1){
                System.out.println("您的输入不合法，请重新输入");
            }else {
                System.out.println("请输入远程仓库的地址：");
                Scanner s = new Scanner(System.in);
                String storePath = s.nextLine();
                pull(storePath);
            }
        } else if(args[0].equals("push")){
            //保持代码健壮性，保证只有git push是合法操作
            if (args.length!=1){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                System.out.println("请输入远程仓库的地址：");
                Scanner s = new Scanner(System.in);
                String storePath = s.nextLine();
                push(storePath);
            }
        }else if(args[0].equals("reflog")){
            //保持代码健壮性，保证只有git reflog是合法操作
            if (args.length!=1){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                System.out.println("执行git reflog操作");
                reflog();
            }
        } else if (args[0].equals("status")) {
            //保持代码健壮性，保证只有git status是合法操作
            if (args.length!=1){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                System.out.println("执行git status操作");
                status();
            }
        }else if (args[0].equals("diff")) {
            //保持代码健壮性，保证只有git diff是合法操作
            if (args.length!=1){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                System.out.println("执行git diff操作");
                diff();
            }
        } else if (args[0].equals("branch")) {
            //确保只有git branch name 是合法输入
            if(args.length!=2){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                branch(args[1]);
            }
        } else if (args[0].equals("find")) {
            //确保只有git find message是合法输入
            if(args.length!=2){
                System.out.println("您的输入不合法，请重新输入");
            }else{
                find(args[1]);
                System.out.println("执行find操作");
            }
        } else {
            System.out.println("您输入的命令有误，请重新输入！");
        }
    }
    //init命令的实现
    public static void init(){
        //首先创建.git文件夹
        File file1 = new File(gitPath);
        if(!file1.exists()){
            file1.mkdirs();
            System.out.println(".git文件夹创建成功");
        }else {
            System.out.println("已存在.git文件夹");
        }
        //创建objects文件夹
        File file2 = new File(objPath);
        if(!file2.exists()){
            file2.mkdirs();
            System.out.println("objects文件夹创建成功");
        }else {
            System.out.println("已存在objects文件夹");
        }
        //创建空白的index文件
        File file3 = new File(indexPath);
        if(!file3.exists()){
            try {
                file3.createNewFile();
                System.out.println("index文件创建成功");
            } catch (IOException e) {
                System.out.println("index文件创建失败");
                throw new RuntimeException(e+e.getMessage());
            }
        }else{
            System.out.println("已存在index文件");
        }
        //创建空白的head文件
        File file4 = new File(headPath);
        if(!file4.exists()){
            try {
                file4.createNewFile();
                System.out.println("head创建成功");
            } catch (IOException e) {
                System.out.println("head创建失败");
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("已存在head文件");
        }
        //创建空白的headN文件用来存放最新的head指针
        File file5 = new File(headNPath);
        if(!file5.exists()){
            try {
                file5.createNewFile();
                System.out.println("headN创建成功");
            } catch (IOException e) {
                System.out.println("headN创建失败");
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("已存在headN文件");
        }
        //创建heads文件夹用来存放所有分支的head指针
        File file6 = new File(headsPath);
        if(!file6.exists()){
            file6.mkdirs();
            System.out.println("heads文件夹创建成功");
        }else {
            System.out.println("已存在heads文件夹");
        }
    }
    //git add name 可以add具体一个文件
    public static void add(String name){
        //add有4种可能性，添加、修改与删除，重复add，所以需要分情况讨论
        //首先需要判断工作区中是否有该文件,不存在则表示add删除的情况
        //此处不会影响addALL的实现，因为addALL遍历工作区文件夹，每一个文件都肯定存在
        File file = new File(userPath+File.separator+name);
        if(!file.exists()){
            //如果该文件在index中有记录，则将index中的记录删除
            //是否有该文件的判断，在函数removeIndex中判断
            removeIndex(name);
        }else{
            //剩下的情况就是工作区中存在该文件，有修改（有可能重复添加）与添加两种可能性
            //首先每一个文件都会生成一个存放在objects文件夹下的blob对象
            Blob blob = new Blob(name);
            //对象序列化写入.objects文件夹
            WriteToObj(blob,objPath);
            //首先对象反序列化，得到目前index文件中的内容,并生成相应的Index对象
            Index inIndexFile = getClassIndex();
            //如果inIndexFile对象中的hashmap中不存在blob对象的原文件名,则表示添加
            if(!inIndexFile.hashMap.containsKey(name)){
                //则把blob的hash值与原文件名name一起加入该hashmap中
                inIndexFile.hashMap.put(name,blob.getSHA1());
                //再把这个更新后的Index对象inIndexFile序列化写入index文件
                IndWriteToInd(inIndexFile,indexPath);
                System.out.println("新文件"+name+"成功add");
            }else{
                //如果在该Index对象的hashmap中存在blob的原文件名
                //此时表示修改文件，也有可能是重复添加文件,所以需要判断
                //如果有一样的key与value表示重复添加，否则为修改文件
                if(sameEntry(inIndexFile.hashMap,name,blob.getSHA1())){
                    System.out.println("重复添加"+name);
                }else{
                    //则把blob的原文件名与新hash值一起加入该hashmap中
                    inIndexFile.hashMap.put(name,blob.getSHA1());
                    //再把这个更新后的Index对象inIndexFile序列化写入index文件
                    IndWriteToInd(inIndexFile,indexPath);
                    System.out.println("文件"+name+"修改后，add成功");
                }
            }
        }
    }

    //如果key在工作区中有，比较文件内容生成的hash值与value
    //k-v均一样，则表示重复添加，对此条entry不作处理
    //k一样，v不同表示修改

    //git add . 即add工作区所有文件的实现
    public static void addAll(){
        //首先寻找在工作区中删除却在index中存在的文件
        //对象反序列化，得到目前index文件中的内容,并生成相应的Index对象
        Index index = getClassIndex();
        //遍历获得key值即文件名，然后与当前的工作区中的文件名比较
        boolean hasDelete = false;
        Set keySet = index.hashMap.keySet();
        //必须建立一个arraylist，否则会报异常
        ArrayList<String> nameList = new ArrayList<>(keySet);
        for(Object k:nameList){
            String key = (String)k;
            String keyPath = userPath+File.separator+key;
            //如果key在工作区中没有，说明该文件已被删除，在index中也删除该entry
            if (!hasFile(keyPath)){
                index.hashMap.remove(key);
                hasDelete = true;
            }
        }
        if(hasDelete){
            //如果对index中的记录做了删除，
            //则将更新后的Index对象序列化输出到index文件中
            IndWriteToInd(index,indexPath);
            System.out.println("成功在index中更新相应记录");
        }
        //其次实现修改与删除操作，遍历工作区所有文件（不包括文件夹）
        File dir=new File(userPath);
        File[] files=dir.listFiles();
        for(File f:files){
            //如果当前File对象为文件
            if(f.isFile()) {
                //获取文件的名字
                String filename = f.getName();
                //如果是MacBook自动创建的.DS_Store文件，则忽略
                if (filename.equals(".DS_Store")) {
                    continue;
                }
                //对每个文件都分别执行add操作
                add(filename);
            }
        }
        System.out.println("成功add所有文件");
    }
    //实现git commit -m "message" 的操作
    public static void commit(String m){
        //反序列化index文件，得到当前Index对象
        //根据index中的记录生成一个Tree对象，并序列化到objects文件夹
        Index index = git.getClassIndex();
        Tree tree = new Tree(index);
        WriteToObj(tree,objPath);
        //同时根据Tree对象和传入的参数：提交信息，创建一个Commit对象
        Commit commit = new Commit(tree,m);
        //将这个Commit对象序列化到objects文件夹
        WriteToObj(commit,objPath);
        //更新head中的最新提交记录
        String commitID = commit.getSHA1();
        //此时应该是覆盖写入，所以不需要加true
        writeHead(commitID,headPath);
        //同时更新headN中的最新提交记录
        //反序列化headN文件，得到HeadN对象
        if(readHead(headNPath).equals("")){
            //则新建一个HeadN对象
            HeadN headN = new HeadN();
            ArrayList list = headN.headList;
            list.add(0,commitID);
            hNWriteTohN(headN,headNPath);
        }else{
            HeadN headN = (HeadN) getClass(headNPath);
            //得到该对象的ArrayList，并且在最前面即下标为0的位置插入id
            ArrayList list = headN.headList;
            list.add(0,commitID);
            //将其序列化进入headN文件中
            hNWriteTohN(headN,headNPath);
        }
        //通过本次Commit对象获得上次Commit对象的id
        String preCommitSHA = commit.getLastCommit();
        //通过上次的SHA1值，在objects文件夹中找到同名的commit文件的绝对地址
        String preCommitPath = objPath+File.separator+preCommitSHA+".txt";
        File preCommitFile = new File(preCommitPath);
        //如果上一次提交的commit文件存在，反序列化该commit文件，恢复为一个Commit对象
        if(preCommitFile.exists()){
            Commit preCommit = (Commit) getClass(preCommitPath);
            //获得该Commit对象指向的preTree对象的hash值
            String preTreeHash = preCommit.getToTree();
            //在objects文件夹中找到该preTree文件
            String preTreePath = objPath+File.separator+preTreeHash+".txt";
            File preTreeFile = new File(preTreePath);
            //如果上一次提交的tree文件存在，反序列化生成Tree对象，获得其treemap
            if(preTreeFile.exists()){
                Tree preTree = (Tree) getClass(preTreePath);
                //对比本次的treemap与上一次的treemap，分情况讨论
                //key：如果本次无，上次有，则为删除
                //key：如果本次有，上次无，则为新增
                //key：如果本次有，上次也有，若value不一样，则为修改，否则不变
                //首先遍历上次的key，如果本次没有则为删除
                Set keySet = preTree.treeMap.keySet();
                for(Object key : keySet){
                    if(!tree.treeMap.containsKey(key)){
                        System.out.println("被删除的文件名为："+key+"\thash值为"+preTree.treeMap.get(key));
                    }
                }
                //遍历本次的key-value
                Set thisKeySet = tree.treeMap.keySet();
                for(Object key : thisKeySet){
                    if(!preTree.treeMap.containsKey(key)){
                        System.out.println("新增的文件名为："+key+"\thash值为"+tree.treeMap.get(key));
                    }else{
                        if(!tree.treeMap.get(key).equals(preTree.treeMap.get(key))){
                            System.out.println("被修改的文件名为："+key+"\t修改后的hash值为"+tree.treeMap.get(key)+"\t修改前的hash值为"+preTree.treeMap.get(key));
                        }
                    }
                }
            }
        }
    }

    //首先实现 git rm --cached name 仅删除index中的entry，不删除工作区中的文件
    public static void removeIndex(String name){
        //首先对象反序列化，从index文件中恢复一个Index的对象
        Index index = getClassIndex();
        //然后获得该Index对象的维护的hashmap，将key=name的删掉
        //首先找在index中是否存在该key值，若存在则删除，否则提示不存在
        if(index.hashMap.containsKey(name)){
            index.hashMap.remove(name);
            System.out.println("成功在index中删除"+name);
        }else{
            System.out.println("抱歉不存在该文件");
        }
        //再将该Index对象反序列化输出到index文件
        IndWriteToInd(index,indexPath);
    }

    //其次实现git rm 某个文件name,在工作区和index中均删除
    public static void removeAll(String name){
        //首先将该文件在index文件夹中删除
        removeIndex(name);
        //然后在工作区中也删除
        File file = new File(userPath+File.separator+name);
        if(file.exists()){
            file.delete();
            System.out.println("成功在工作区删除"+name);
        }
    }

    //实现git log 操作
    public static void log(){
        //从HEAD文件中读出一串字符串，得到最近一次commit的hash值
        String hash = readHead(headPath);
        if(hash==null){
            System.out.println("目前还没有提交记录");
        }
        //当hash值非空时，反复进行根据hash值反序列化-得到前一次提交hash值的操作
        while(hash!=null){
            //通过该hash值找到相应文件路径
            String filePath = objPath+File.separator+hash+".txt";
            //如果找到了file，则反序列化生成一个Commit对象
            File file = new File(filePath);
            if(file.exists()){
                Commit commit = (Commit) getClass(filePath);
                //打印相关记录
                System.out.println(hash+"\t"+commit.getMessage()+"\t"+commit.getCommitTime());
                //得到前一次的commit的hash值
                String preCommitHash = commit.getLastCommit();
                //令hash值等于前一次hash值，循环操作
                hash = preCommitHash;
            }else {
                break;
            }
        }
    }

    //实现git reset --soft 某次提交的commitID
    public static void resetSoft(String commitHash){
        String commitPath = objPath+File.separator+commitHash+".txt";
        File file = new File(commitPath);
        //如果objects文件夹中存在这次commitID
        if(file.exists()){
            //则将这个给定的hash值覆盖写入head中
            writeHead(commitHash,headPath);
        }
        //同时更新headN中的head变动情况
        //反序列化headN文件，得到HeadN对象
        HeadN headN = (HeadN) getClass(headNPath);
        //得到该对象的ArrayList，并且在最前面即下标为0的位置插入id
        ArrayList list = headN.headList;
        list.add(0,commitHash);
        //将其序列化进入headN文件中
        hNWriteTohN(headN,headNPath);
    }

    //实现git reset --mixed 某次提交的commitID
    public static void resetMixed(String commitHash){
        //得到该commitID的文件路径
        String commitPath = objPath+File.separator+commitHash+".txt";
        File file = new File(commitPath);
        //如果objects文件夹中存在这次commitID对应的文件
        if(file.exists()){
            //则将这个给定的hash值覆盖写入head中
            writeHead(commitHash,headPath);
            //反序列化headN文件，得到HeadN对象
            HeadN headN = (HeadN) getClass(headNPath);
            //得到该对象的ArrayList，并且在最前面即下标为0的位置插入id
            ArrayList list = headN.headList;
            list.add(0,commitHash);
            //将其序列化进入headN文件中
            hNWriteTohN(headN,headNPath);
            //反序列化该commit文件
            Commit commit = (Commit) getClass(commitPath);
            //由于tree文件相当于是对当时提交时index文件的截图,
            //故而得到该commit文件指向的tree的hash值
            String treeHash = commit.getToTree();
            //得到tree文件的路径
            String treePath = objPath+File.separator+treeHash+".txt";
            //反序列化该tree文件，得到Tree对象
            Tree tree = (Tree) getClass(treePath);
            //得到tree的hashmap，也就得到了当时index中的hashmap
            Index indexCommit = new Index(tree.treeMap);
            //将这个Index对象序列化到index文件中
            IndWriteToInd(indexCommit,indexPath);
            System.out.println("git reset --mixed成功");
        }
    }

    //实现git reset --hard，连工作区也要重新设置
    public static void resetHard(String commitHash,String thisHeadPath,String thisIndexPath,String thisWorkPath){
        //得到该commitID的文件路径
        String commitPath = objPath+File.separator+commitHash+".txt";
        File file = new File(commitPath);
        //如果objects文件夹中存在这次commitID对应的commit文件
        //并且如果这个文件是Commit类型的文件则继续操作
        Object obj = getClass(commitPath);
        if(file.exists()&& (obj instanceof Commit)){
            //则将这个给定的hash值覆盖写入head中
            writeHead(commitHash,thisHeadPath);
            //反序列化headN文件，得到HeadN对象
            File file1 = new File(headNPath);
            if(file1.exists()){
                HeadN headN = (HeadN) getClass(headNPath);
                //得到该对象的ArrayList，并且在最前面即下标为0的位置插入id
                ArrayList list = headN.headList;
                list.add(0,commitHash);
                //将其序列化进入headN文件中
                hNWriteTohN(headN,headNPath);
            }
            //反序列化该commit文件
            Commit commit = (Commit) obj;
            //由于tree文件相当于是对当时提交时index文件的截图,
            //故而得到该commit文件指向的tree的hash值
            String treeHash = commit.getToTree();
            //得到tree文件的路径
            String treePath = objPath+File.separator+treeHash+".txt";
            //反序列化该tree文件，得到Tree对象
            Tree tree = (Tree) getClass(treePath);
            //得到tree的hashmap，也就得到了当时index中的hashmap
            Index indexCommit = new Index(tree.treeMap);
            //将这个Index对象序列化到index文件中
            IndWriteToInd(indexCommit,thisIndexPath);

            //遍历工作区文件，用map.contains(name)
            //必须把获得当前工作区的文件对象数组的步骤放在循环外面！
            File dir=new File(thisWorkPath);
            File[] files=dir.listFiles();
            for(File f:files){
                //如果当前File对象为文件
                if(f.isFile()){
                    //获取文件的名字
                    String filename = f.getName();
                    //如果工作区中的文件在map中没有，则删除该文件
                    if(!indexCommit.hashMap.containsKey(filename)){
                        f.delete();
                    }
                }
            }
            //遍历k
            Set keySet = indexCommit.hashMap.keySet();
            for(Object k : keySet){
                //首先看k，如果map中的k文件名在工作区中没有，用file.exist()，则新建该文件
                String key = (String)k;
                String keyPath = thisWorkPath+File.separator+key;
                File newFile = new File(keyPath);
                if(!newFile.exists()){
                    System.out.println(keyPath+"不存在");
                    try {
                        newFile.createNewFile();
                        //根据Index对象维护的hashmap获取之前的文件内容
                        String preContent = getPreContent(indexCommit,key);
                        Methods.writeFileContent(keyPath,preContent);
                        System.out.println("创建成功"+newFile.getName());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }else {
                    //k在工作区中存在，给定k，比较value(map.get(k))与工作区中文件的hash值
                    if(!indexCommit.hashMap.get(key).equals(Methods.calSHA1(keyPath))){
                        //不一样，则覆盖写入原文件内容
                        //根据Index对象维护的hashmap获取之前的文件内容
                        String preContent = getPreContent(indexCommit,key);
                        //将其覆盖写入目标文件
                        Methods.writeFileContent(keyPath,preContent);
                    }
                }
            }
        }else {
            System.out.println("输入的commitID有误，请重新输入");
        }
    }

    //git pull 将代码从远程仓库拉到本地，参数为远程仓库的路径（类似于工作区userPath）
    public static void pull(String storePath){
        //其实就是push中，接收方和发送方的路径对调
        //首先写出发送方（远程仓库）的相关文件的路径
        String gitTransPath = storePath+File.separator+".git";
        String headTransPath = gitTransPath+File.separator+"head.txt";
        String indexTransPath = gitTransPath+File.separator+"index.txt";
        String objTransPath = gitTransPath+File.separator+"objects";
        String headNTransPath = gitTransPath+File.separator+"headN.txt";
        //判断接收方的仓库中是否有.git文件夹和head、index文件、objects文件夹
        //如果没有，则对接收方重新创建.git文件夹，在其中创建head和index的空白文件以及空的objects文件夹
        File fileGit = new File(gitPath);
        if (!fileGit.exists()){
            fileGit.mkdirs();
            System.out.println("接收方的.git文件夹创建成功");
        }
        File fileHead = new File(headPath);
        if (!fileHead.exists()){
            try {
                fileHead.createNewFile();
                System.out.println("接收方的head文件创建成功");
            } catch (IOException e) {
                throw new RuntimeException(e + e.getMessage());
            }
        }
        File fileIndex = new File(indexPath);
        if (!fileIndex.exists()){
            try {
                fileIndex.createNewFile();
                System.out.println("接收方的index文件创建成功");
            } catch (IOException e) {
                throw new RuntimeException(e + e.getMessage());
            }
        }
        File fileObj = new File(objPath);
        if (!fileObj.exists()){
            fileObj.mkdirs();
            System.out.println("接收方的objects文件夹创建成功");
        }
//        File fileHeadN = new File(headNPath);
//        if (!fileHeadN.exists()){
//            try {
//                fileHeadN.createNewFile();
//                System.out.println("接收方的headN文件创建成功");
//            } catch (IOException e) {
//                throw new RuntimeException(e + e.getMessage());
//            }
//        }
        System.out.println("请输入一个端口号");
        Scanner s = new Scanner(System.in);
        int portID = s.nextInt();
        //首先pull整个objects文件夹中的文件
        //传送方传整个.objects文件夹中的文件到接收方的objects文件夹
        //遍历发送方objects文件夹下的所有文件，并依次执行传送操作
        File dir=new File(objTransPath);
        File[] files=dir.listFiles();
        for(File f:files){
            //如果是一个文件，则首先传送方传送，建立Socket，将其传输
            if(f.isFile()){
                //获得文件的地址
                String name = f.getName();
                String filePath = objTransPath+File.separator+name;
                //一定要注意，在远程仓库重建时，会自动生成.DS_Store
                if (name.equals(".DS_Store")) {
                    continue;
                }
                //首先考虑接受端，也要准备
                //新建一个服务器对象，此时处于等待倾听的阶段
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(portID);
                    System.out.println("成功建立服务器，等待客户发送请求");
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    if(e.getMessage().equals("Address already in use")){
                        System.out.println("该端口号已被占用，其重新设置");
                    }
                    throw new RuntimeException(e);
                }
                //客户端首先要发送连接请求给服务器
                Socket transSocket;
                try {
                    transSocket = new Socket("127.0.0.1",portID);
                    System.out.println("客户已成功发送请求");
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    if(e.getMessage().equals("Address already in use")){
                        System.out.println("该端口号已被占用，其重新设置");
                    }
                    throw new RuntimeException(e);
                }
                //此时客户端会发送一个连接请求，如果接收的话，会建立一个新的Socket
                Socket recSocket;
                {
                    try {
                        recSocket = serverSocket.accept();
                        System.out.println("已接收客户请求，成功建立连接");
                    } catch (IOException e) {
                        System.out.println("出现错误，错误为："+e.getMessage());
                        System.out.println("接收客户请求失败，请重新尝试");
                        throw new RuntimeException(e);
                    }
                }
                //发送方发送文件
                Methods.transmitFile(filePath,transSocket);
                //接收方接受文件
                Methods.receiveFile(objPath,recSocket);
                try {
                    transSocket.close();
                    recSocket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        //从传送方的head文件，获取到最近一次的commit id
        String commitHash = readHead(headTransPath);
        //然后pull head文件和index文件以及工作区中的文件

        //对接收方执行reset --hard命令
        //得到该commitID的文件路径
        if(!commitHash.equals("")){
            String commitPath = objPath+File.separator+commitHash+".txt";
            File file = new File(commitPath);
            //如果objects文件夹中存在这次commitID对应的commit文件
            //并且如果这个文件是Commit类型的文件则继续操作
            Object obj = getClass(commitPath);
            if(file.exists()&& (obj instanceof Commit)){
                //则将这个给定的hash值覆盖写入head中
                writeHead(commitHash,headPath);
                //反序列化该commit文件
                Commit commit = (Commit) obj;
                //由于tree文件相当于是对当时提交时index文件的截图,
                //故而得到该commit文件指向的tree的hash值
                String treeHash = commit.getToTree();
                //得到tree文件的路径
                String treePath = objPath+File.separator+treeHash+".txt";
                //反序列化该tree文件，得到Tree对象
                Tree tree = (Tree) getClass(treePath);
                //得到tree的hashmap，也就得到了当时index中的hashmap
                Index indexCommit = new Index(tree.treeMap);
                //将这个Index对象序列化到index文件中
                IndWriteToInd(indexCommit,indexPath);

                //遍历工作区文件，用map.contains(name)
                //必须把获得当前工作区的文件对象数组的步骤放在循环外面！
                File dir2=new File(userPath);
                File[] files2=dir2.listFiles();
                for(File f:files2){
                    //如果当前File对象为文件
                    if(f.isFile()){
                        //获取文件的名字
                        String filename = f.getName();
                        //如果工作区中的文件在map中没有，则删除该文件
                        if(!indexCommit.hashMap.containsKey(filename)){
                            f.delete();
                        }
                    }
                }
                //遍历k
                Set keySet = indexCommit.hashMap.keySet();
                for(Object k : keySet){
                    //首先看k，如果map中的k文件名在工作区中没有，用file.exist()，则新建该文件
                    String key = (String)k;
                    String keyPath = userPath+File.separator+key;
                    File newFile = new File(keyPath);
                    if(!newFile.exists()){
                        System.out.println(keyPath+"不存在");
                        try {
                            newFile.createNewFile();
                            //根据Index对象维护的hashmap获取之前的文件内容
                            String preContent = getPreContent(indexCommit,key);
                            Methods.writeFileContent(keyPath,preContent);
                            System.out.println("创建成功"+newFile.getName());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else {
                        //k在工作区中存在，给定k，比较value(map.get(k))与工作区中文件的hash值
                        if(!indexCommit.hashMap.get(key).equals(Methods.calSHA1(keyPath))){
                            //不一样，则覆盖写入原文件内容
                            //根据Index对象维护的hashmap获取之前的文件内容
                            String preContent = getPreContent(indexCommit,key);
                            //将其覆盖写入目标文件
                            Methods.writeFileContent(keyPath,preContent);
                        }
                    }
                }
            }else {
                System.out.println("commitID有误");
            }
        }
    }

    //git push 将本地的代码推到远程仓库
    public static void push(String storePath){
        //首先写出接收方相关文件的路径
        String gitRePath = storePath+File.separator+".git";
        String headRePath = gitRePath+File.separator+"head.txt";
        String indexRePath = gitRePath+File.separator+"index.txt";
        String objRePath = gitRePath+File.separator+"objects";
        String headNRePath = gitRePath+File.separator+"headN.txt";
        //判断接收方的仓库中是否有.git文件夹和head、index文件、objects文件夹
        //如果没有，则对接收方重新创建.git文件夹，在其中创建head和index的空白文件以及空的objects文件夹
        File fileGit = new File(gitRePath);
        if (!fileGit.exists()){
            fileGit.mkdirs();
            System.out.println("接收方的.git文件夹创建成功");
        }
        File fileHead = new File(headRePath);
        if (!fileHead.exists()){
            try {
                fileHead.createNewFile();
                System.out.println("接收方的head文件创建成功");
            } catch (IOException e) {
                throw new RuntimeException(e + e.getMessage());
            }
        }
//        File fileNHead = new File(headNRePath);
//        if (!fileNHead.exists()){
//            try {
//                fileNHead.createNewFile();
//                System.out.println("接收方的headN文件创建成功");
//            } catch (IOException e) {
//                throw new RuntimeException(e + e.getMessage());
//            }
//        }
        File fileIndex = new File(indexRePath);
        if (!fileIndex.exists()){
            try {
                fileIndex.createNewFile();
                System.out.println("接收方的index文件创建成功");
            } catch (IOException e) {
                throw new RuntimeException(e + e.getMessage());
            }
        }
        File fileObj = new File(objRePath);
        if (!fileObj.exists()){
            fileObj.mkdirs();
            System.out.println("接收方的objects文件夹创建成功");
        }
        //首先pull整个objects文件夹中的文件
        //传送方传整个.objects文件夹中的文件到接收方的objects文件夹
            //遍历发送方objects文件夹下的所有文件，并依次执行传送操作
        File dir=new File(objPath);
        File[] files=dir.listFiles();
        System.out.println("请输入一个端口号");
        Scanner s = new Scanner(System.in);
        int portID = s.nextInt();
        for(File f:files){
            //如果是一个文件，则首先传送方传送，建立Socket，将其传输
            if(f.isFile()){
                //首先考虑接受端，也要准备
                //新建一个服务器对象，此时处于等待倾听的阶段
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(portID);
                    System.out.println("成功建立服务器，等待客户发送请求");
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    if(e.getMessage().equals("Address already in use")){
                        System.out.println("该端口号已被占用，其重新设置");
                    }
                    throw new RuntimeException(e);
                }
                //客户端首先要发送连接请求给服务器
                Socket transSocket;
                try {
                    transSocket = new Socket("127.0.0.1",portID);
                    System.out.println("客户已成功发送请求");
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    if(e.getMessage().equals("Address already in use")){
                        System.out.println("该端口号已被占用，其重新设置");
                    }
                    throw new RuntimeException(e);
                }
                //此时客户端会发送一个连接请求，如果接收的话，会建立一个新的Socket
                Socket recSocket;
                {
                    try {
                        recSocket = serverSocket.accept();
                        System.out.println("已接收客户请求，成功建立连接");
                    } catch (IOException e) {
                        System.out.println("出现错误，错误为："+e.getMessage());
                        System.out.println("接收客户请求失败，请重新尝试");
                        throw new RuntimeException(e);
                    }
                }
                //获得文件的地址
                String name = f.getName();
                String filePath = objPath+File.separator+name;
                //发送方发送文件
                Methods.transmitFile(filePath,transSocket);
                //接收方接受文件
                Methods.receiveFile(objRePath,recSocket);
                try {
                    transSocket.close();
                    recSocket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("出现错误，错误为："+e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        //从传送方的head文件，获取到最近一次的commit id
        String commitHash = readHead(headPath);
        //然后pull head文件和index文件以及工作区中的文件
        //对接收方执行reset --hard命令
        String commitPath = objPath+File.separator+commitHash+".txt";
        File file = new File(commitPath);
        //如果objects文件夹中存在这次commitID对应的commit文件
        //并且如果这个文件是Commit类型的文件则继续操作
        Object obj = getClass(commitPath);
        if(file.exists()&& (obj instanceof Commit)){
            //则将这个给定的hash值覆盖写入head中
            writeHead(commitHash,headRePath);
            //反序列化headN文件，得到HeadN对象
            //反序列化该commit文件
            Commit commit = (Commit) obj;
            //由于tree文件相当于是对当时提交时index文件的截图,
            //故而得到该commit文件指向的tree的hash值
            String treeHash = commit.getToTree();
            //得到tree文件的路径
            String treePath = objPath+File.separator+treeHash+".txt";
            //反序列化该tree文件，得到Tree对象
            Tree tree = (Tree) getClass(treePath);
            //得到tree的hashmap，也就得到了当时index中的hashmap
            Index indexCommit = new Index(tree.treeMap);
            //将这个Index对象序列化到index文件中
            IndWriteToInd(indexCommit,indexRePath);

            //遍历工作区文件，用map.contains(name)
            //必须把获得当前工作区的文件对象数组的步骤放在循环外面！
            File dir2=new File(storePath);
            File[] files2=dir2.listFiles();
            for(File f:files2){
                //如果当前File对象为文件
                if(f.isFile()){
                    //获取文件的名字
                    String filename = f.getName();
                    //如果工作区中的文件在map中没有，则删除该文件
                    if(!indexCommit.hashMap.containsKey(filename)){
                        f.delete();
                    }
                }
            }
            //遍历k
            Set keySet = indexCommit.hashMap.keySet();
            for(Object k : keySet){
                //首先看k，如果map中的k文件名在工作区中没有，用file.exist()，则新建该文件
                String key = (String)k;
                String keyPath = storePath+File.separator+key;
                File newFile = new File(keyPath);
                if(!newFile.exists()){
                    System.out.println(keyPath+"不存在");
                    try {
                        newFile.createNewFile();
                        //根据Index对象维护的hashmap获取之前的文件内容
                        String preContent = getPreContent(indexCommit,key);
                        Methods.writeFileContent(keyPath,preContent);
                        System.out.println("创建成功"+newFile.getName());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }else {
                    //k在工作区中存在，给定k，比较value(map.get(k))与工作区中文件的hash值
                    if(!indexCommit.hashMap.get(key).equals(Methods.calSHA1(keyPath))){
                        //不一样，则覆盖写入原文件内容
                        //根据Index对象维护的hashmap获取之前的文件内容
                        String preContent = getPreContent(indexCommit,key);
                        //将其覆盖写入目标文件
                        Methods.writeFileContent(keyPath,preContent);
                    }
                }
            }
        }else {
            System.out.println("commitID有误");
        }
    }

    //实现git reflog的操作
    //git reflog 类似于后悔药，在用户实现 git reset --hard 后，
    //head指针指向了之前的commit id，如果想回退找到之后的commit id，就需要用git reflog
    //即git reflog 打印的是head指针的变动情况
    //建立一个headN类，里面维护了一个ArrayList数组，当中存放的是每一次head文件指向的id
    public static void reflog(){
        //反序列化headN文件，得到HeadN对象
        //首先，进行异常处理，如果headN中没有任何内容
        if(readHead(headNPath).equals("")){
            System.out.println("当前没有head变动相关记录");
        }else{
            HeadN headN = (HeadN) getClass(headNPath);
            //得到该对象的ArrayList，下标为0的即为最新的，打印HEAD@{0}
            ArrayList list = headN.headList;
            for(int i=0;i<list.size();i++){
                String hash = (String) list.get(i);
                //找到相应commit对象，得到提交信息
                if(hash!=null){
                    Commit commit = (Commit)getClass(objPath+File.separator+hash+".txt");
                    System.out.println(hash+"\t"+ "HEAD@{"+i+"}"+"\t"+commit.getMessage());
                }
            }
        }
    }
    //git status查看文件状态
    //文件有四种状态，Untracked: 文件在工作区中但没有add
    //Modified: 文件已修改，但修改操作未add
    //Deleted：文件已删除，但删除操作未add
    //Staged：文件已add但没有commit
    public static void status(){
        //首先创建四个ArrayList用来保存已经判断过类型的文件
        ArrayList<String> untracked = new ArrayList<>();
        ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> deleted = new ArrayList<>();
        ArrayList<String> staged = new ArrayList<>();
        //1.判断Untracked：当前index map中没有k，但工作区中有
        //2.判断Modified：工作区中文件与当前index的map中k同，v不同
        //首先反序列化当前的index文件为一个Index对象
        Index index = getClassIndex();
        //再获得该Index对象的hashmap
        HashMap hashMap = index.hashMap;
        //遍历工作区文件，依次寻找是否在map中存在一样的k值
        File dir=new File(userPath);
        File[] files=dir.listFiles();
        for(File f:files){
            //如果当前File对象为文件
            if(f.isFile()) {
                //获取文件的名字
                String filename = f.getName();
                //如果是MacBook自动创建的.DS_Store文件，则忽略
                if (filename.equals(".DS_Store")) {
                    continue;
                }
                //如果当前的文件名不在hashmap中，则是Untracked
                if(!hashMap.containsKey(filename)){
                    untracked.add(filename);
                }
                //获得当前文件的路径
                String filePath = userPath + File.separator + filename;
                //获得当前文件的hash值
                String hash = Methods.calSHA1(Methods.getFileContent(filePath));
                //如果当前文件与当前index的map中k同，v不同，则是Modified
                if((hashMap.containsKey(filename))&&!(hashMap.get(filename).equals(hash))){
                    modified.add(filename);
                }
            }
        }
        //3.判断Deleted：目前index的map中有，但是工作区没有
        //首先获得该hashmap的key集合
        Set keySet = hashMap.keySet();
        //遍历该集合，对每一个key，看是否在工作区中存在
        for (Object key : keySet){
            String name = (String) key;
            String path = userPath + File.separator+name;
            File file = new File(path);
            //如果不存在，则表明是被删除的文件
            if(!file.exists()){
                deleted.add(name);
            }
        }
        //4.判断staged：已add未commit，
        //即在最新commit的index中没有该文件，表示未commit
        //而本次的index中有这个文件，表示已add
        //首先找head，得到最近的commit id
        String lastCID = readHead(headPath);
        //控制代码稳健型，如果之前有过commit，即head文件非空
        if(!lastCID.equals("")){
            //反序列化最新的commit文件为Commit对象
            Commit commit = (Commit) getClass(objPath+File.separator+lastCID+".txt");
            //通过该Commit对象，得到当时tree的ID
            String treeID = commit.getToTree();
            //反序列化为一个Tree对象，得到treemap，即最新commit的index
            Tree tree = (Tree) getClass(objPath+File.separator+treeID+".txt");
            HashMap treemap = tree.treeMap;
            //遍历index的map
            for (Object key : keySet){
                //对每一个key，若在treemap中没有，则是staged
                if(!treemap.containsKey(key)){
                    staged.add((String) key);
                }
            }
        }else{
            //如果之前没有过commit,本次的index中有这个文件，表示已add
            for (Object key : keySet){
                staged.add((String) key);
            }
        }
        //判断arraylist中是否有需要输出的内容
        if(!untracked.isEmpty()){
            System.out.println("Untracked files:");
            for (String name : untracked) {
                System.out.println("\tuntracked:"+name);
            }
        }
        if(!modified.isEmpty()||!deleted.isEmpty()){
            System.out.println("Changes not staged for commit :");
            if(!modified.isEmpty()){
                for (String name : modified) {
                    System.out.println("\tmodified:"+name);
                }
            }
            if(!deleted.isEmpty()){
                for (String name : deleted) {
                    System.out.println("\tdeleted:"+name);
                }
            }
        }
        if(!staged.isEmpty()){
            System.out.println("Changes to be committed:");
            for (String name : staged) {
                System.out.println("\tstaged:"+name);
            }
        }
        if(modified.isEmpty()&&untracked.isEmpty()&&deleted.isEmpty()&&staged.isEmpty()){
            System.out.println("nothing to commit,working tree clean");
        }
    }

    //git diff 展示文件具体内容的修改
    public static void diff(){
        //首先找到修改了的文件
        //首先反序列化当前的index文件为一个Index对象
        Index index = getClassIndex();
        //再获得该Index对象的hashmap
        HashMap hashMap = index.hashMap;
        //遍历工作区文件
        File dir=new File(userPath);
        File[] files=dir.listFiles();
        for(File f:files){
            //如果当前File对象为文件
            if(f.isFile()) {
                //获取文件的名字
                String filename = f.getName();
                //如果是MacBook自动创建的.DS_Store文件，则忽略
                if (filename.equals(".DS_Store")) {
                    continue;
                }
                //获得当前文件的路径
                String filePath = userPath + File.separator + filename;
                //获得当前文件的hash值
                String hash = Methods.calSHA1(Methods.getFileContent(filePath));
                //根据index中的v，获得之前的hash值
                String preHash = (String) hashMap.get(filename);
                //如果当前文件与当前index的map中k同，v不同，则是修改的文件
                //如果之前的prehash不存在，为null；即之前没有add过，必须处理，否则会有空指针异常
                if(preHash==null){
                    continue;
                }else{
                    if(!preHash.equals(hash)){
                        //根据之前的hash值，找到相应的blob文件
                        String path = objPath+File.separator+preHash+".txt";
                        //反序列化该文件，得到一个Blob对象
                        Blob blob = (Blob) getClass(path);
                        //获得该对象的content，即之前的文件内容
                        String preContent = blob.getContent();
                        //获得现在的文件内容
                        String nowContent = Methods.getFileContent(filePath);
                        System.out.println(filename);
                        //-表示删除的内容，+表示修改后的内容
                        System.out.println("-"+preContent);
                        System.out.println("+"+nowContent);
                    }
                }
            }
        }
    }

    //git branch name 新增分支名为name的分支
    public static void branch(String branchName){
        //即在heads文件夹中添加一个新的名为branchName的文件，内容为当前的commitID
        //首先写出该分支指针的地址
        String branchPath = headsPath+File.separator+branchName+".txt";
        //得到当前的commitID
        String commitID = readHead(headPath);
        //创建空白的branch head文件
        File file = new File(branchPath);
        if(!file.exists()){
            try {
                file.createNewFile();
                System.out.println("分支"+branchName+"创建成功");
            } catch (IOException e) {
                System.out.println("分支"+branchName+"创建失败");
                throw new RuntimeException(e + e.getMessage());
            }
        }else{
            System.out.println("已存在该分支，请重新输入分支名");
        }
        //将当前的commitID写入
        writeHead(commitID,branchPath);
    }

    //实现git rm --branch name
    public static void removeB(String branchName){
        //首先判断，如果想要删除的branchName不存在，不能删除
        String hContent = readHead(headPath);
        String branchPath = headsPath+File.separator+branchName+".txt";
        File file = new File(branchPath);
        if(!file.exists()){
            System.out.println("不存在分支"+branchName);
        }else if(hContent.equals(readHead(branchPath))){
            //如果该branch是现在的head指向的branch，则不能删除
            //分别读取head与该分支文件的内容，若相等则不能删除
            System.out.println("不能删除当前分支");
        } else {
            //以上情况满足，则直接删掉heads文件夹中的该文件即可
            file.delete();
            System.out.println("成功删除分支"+branchName);
        }
    }

    //实现git find，打印所有与输入message相同的Commit的ID
    public static void find(String mes){
        //从HEAD文件中读出一串字符串，得到最近一次commit的hash值
        String hash = readHead(headPath);
        if(hash==null){
            System.out.println("目前还没有提交记录");
        }
        //当hash值非空时，反复进行根据hash值反序列化-得到前一次提交hash值的操作
        while(hash!=null){
            //通过该hash值找到相应文件路径
            String filePath = objPath+File.separator+hash+".txt";
            //如果找到了file，则反序列化生成一个Commit对象
            File file = new File(filePath);
            if(file.exists()){
                Commit commit = (Commit) getClass(filePath);
                //比较其信息是否与目标信息相同,相同则打印
                if(commit.getMessage().equals(mes)){
                    System.out.println(hash);
                }
                //得到前一次的commit的hash值
                String preCommitHash = commit.getLastCommit();
                //令hash值等于前一次hash值，循环操作
                hash = preCommitHash;
            }else {
                break;
            }
        }
    }

    //序列化blob、tree、commit文件进入objects文件夹
    public static void WriteToObj(Object obj,String path){
        //首先得到以原文件内容的hash值命名的新文件的绝对地址
        String sha1 = "";
        if(obj instanceof Tree){
            sha1 = ((Tree) obj).getSHA1();
        } else if (obj instanceof Commit) {
            sha1 = ((Commit)obj).getSHA1();
        } else{
            sha1 = ((Blob)obj).getSHA1();
        }
        String Path = path+File.separator+sha1+".txt";
        if(hasFile(Path)){
            System.out.println("该对象已存在");
        }else{
            //通过序列化，将对象的状态写在流里面
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream(Path));
                outputStream.writeObject(obj);
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    //根据文件的绝对路径判断是否存在某个文件
    public static boolean hasFile(String path){
        File file = new File(path);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
    //Object对象反序列化，将输入流中的状态恢复成一个Object对象
    public static Object getClass(String Path){
        Object res ;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(Path));
            //通过反序列化创建一个对象
            //当没有读到最后的结尾时
                res = objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("出现了异常，异常信息为"+e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("出现了异常，异常信息为"+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    //Index对象反序列化，将输入流中的状态恢复成一个Index对象
    public static Index getClassIndex(){
        Index index;
        File f = new File(indexPath);
        //如果index文件中什么也没有
        if (f.length ( ) == 0) {
            index = new Index();
        } else {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(indexPath));
                //通过反序列化创建一个对象,记得强转一下
                index = (Index) objectInputStream.readObject();
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            } finally {
                try {
                    if (objectInputStream != null) {
                        objectInputStream.close();
                    }
                } catch (IOException e) {
                    System.out.println("出现了异常，异常信息为"+e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        return index;
    }
    //Index对象序列化,写入index文件中
    public static void IndWriteToInd(Index index,String thisIndexPath){
        //通过序列化，将index对象的状态写在流里面
        ObjectOutputStream outputStream = null;
        try {
            //此处必须以覆盖的方式写入，所以不能加上参数true
            outputStream = new ObjectOutputStream(new FileOutputStream(thisIndexPath));
            outputStream.writeObject(index);
        } catch (IOException e) {
            System.out.println("出现了异常，异常信息为"+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
    //把HeadN对象序列化到headN文件中
    public static void hNWriteTohN(HeadN headN,String thisHeadNPath){
        //通过序列化，将headN对象的状态写在流里面
        ObjectOutputStream outputStream = null;
        try {
            //此处必须以覆盖的方式写入，所以不能加上参数true
            outputStream = new ObjectOutputStream(new FileOutputStream(thisHeadNPath));
            outputStream.writeObject(headN);
        } catch (IOException e) {
            System.out.println("出现了异常，异常信息为"+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    //遍历hashmap中的value值,看在key相等的情况下，是否存在相同的value值
    //即判断是否有一样的entry
    public static boolean sameEntry(HashMap<String,String> hashMap,String k,String v){
        boolean same = false;
        Set entrySet = hashMap.entrySet();
        for(Object e : entrySet){
            Map.Entry entry = (Map.Entry) e;
            if(entry.getKey().equals(k)&&entry.getValue().equals(v)){
                return true;
            }
        }
        return same;
    }
    //将一个字符串写入head文件中
    public static void writeHead(String s,String thisHeadPath){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(thisHeadPath);
            fileWriter.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
    //从HEAD中读出一个字符串
    public static String readHead(String thisHeadPath){
        String hash = "";
        int readLen = 0;
        FileReader fileReader = null;
        char[] buf = new char[10];
        try {
            fileReader = new FileReader(thisHeadPath);
            while((readLen = fileReader.read(buf))!=-1){
                hash += new String(buf,0,readLen);
            }
        } catch (IOException e) {
            System.out.println("出现了异常，异常信息为"+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                System.out.println("出现了异常，异常信息为"+e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return hash;
    }

    //根据某一Index对象中的hash值，反序列化得到index文件生成时刻该文件的文件内容
    public static String getPreContent(Index indexCommit,String filename){
        //根据hash值可以得到objects文件夹中的blob文件的绝对路径
        String blobHash = indexCommit.hashMap.get(filename);
        String blobPath = objPath+File.separator+blobHash+".txt";
        //反序列化生成Blob对象，即可得到工作区中文件的内容
        Blob blob = (Blob)getClass(blobPath);
        String content = blob.getContent();
        return content;
    }
}

//index文件中，存放的是blob的entry（k-v），相当于每一次add的一个截屏
//index对象中，存放的是一个hashMap,key为blob对象的原文件名，v为每个工作区中文件的hash值
//这样设计,如果内容有修改，则可以直接替换entry
class Index implements Serializable{
    HashMap<String,String> hashMap;

    public Index(){
        //必须要new一个，否则会有空指针异常
        hashMap = new HashMap<>();
    }

    public Index(HashMap<String,String> hashMap){
        this.hashMap = hashMap;
    }

    //重写Index类的方法，改为每一条entry的hash值与原文件名的记录
    //可以直接把Index对象，对象序列化到index文件中
    //直接输出对象，输出的将是我们定义的字符串
    @Override
    public String toString() {
        String res = "";
        //遍历得到entrySet每个k-v，即hash值与blob.originalName
        Set entrySet = hashMap.entrySet();
        for (Object entry: entrySet) {
            Map.Entry m = (Map.Entry) entry;
            //把每一条entry的文件名与hash值的组合写入结果字符串中
            res += m.getKey() + "-" + m.getValue();
        }
        return res;
    }
}


//每一个工作区中的文件，都会生成一个blob对象，其中存放的是文件的内容
class Blob implements Serializable{
    private String originalName;
    private int size;
    private String content;
    //加上static表示SHA1属性不参与序列化与反序列化
    static private String SHA1;
    //根据文件名可以新建一个Blob对象
    public Blob(String originalName){
        this.originalName = originalName;
        content = Methods.getFileContent(git.userPath+ File.separator+originalName);
        size = Methods.calSizeLen(content);
        SHA1 = Methods.calSHA1(content);
    }

    public String getSHA1() {
        return Methods.calSHA1(content);
    }

    public String getContent() {
        return content;
    }

    public String getOriginalName() {
        return originalName;
    }

    public int getSize() {
        return size;
    }
    //重写toString方法，这样序列化Blob对象时，可以写入我们定义的内容
    @Override
    public String toString() {
        return "blob{" +
                "size=" + size +
                ", content='" + content + '\'' +
                '}';
    }
}

//tree对象中存放的是文件夹的内容,commit时会根据文件夹的内容生成tree类型的文件
//tree中也维护了一个HashMap,存放了文件夹中的每个文件的文件名-hash值
class Tree implements Serializable{
    HashMap<String,String> treeMap;
    static private String SHA1;

    public String getSHA1() {
        return Methods.calSHA1(treeMap.toString());
    }

    public HashMap<String, String> getTreeMap() {
        return treeMap;
    }

    public Tree(Index index){
        //当前Index对象的hashmap，即为tree的treemap
        treeMap = index.hashMap;
        SHA1 = Methods.calSHA1(treeMap.toString());
    }

    @Override
    public String toString() {
        return "Tree{" +
                "treeMap=" + treeMap +
                '}';
    }
}

//Commit对象
class Commit implements Serializable{
    //保持序列化与反序列化的版本兼容性
    private static final long serialVersionUID = 1L;
    private String message;
    private String commitTime;
    private String lastCommit;
    private String toTree;
    //加上static表示SHA1不参与序列化与反序列化
    static private String SHA1;
    public Commit(Tree tree,String message){
        this.message = message;
        commitTime = Methods.getTime();
        lastCommit = Methods.getFileContent(git.headPath);
        toTree = tree.getSHA1();
        //注意！不能包括commitTime，否则每次都会生成不一样的SHA1值
        SHA1 = Methods.calSHA1(message+toTree);
    }
    public String getSHA1() {
        return Methods.calSHA1(message+toTree);
    }

    public String getMessage() {
        return message;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public String getLastCommit() {
        return lastCommit;
    }

    public String getToTree() {
        return toTree;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", commitTime='" + commitTime + '\'' +
                ", lastCommit='" + lastCommit + '\'' +
                ", toTree='" + toTree + '\'' +
                '}';
    }
}
class HeadN implements Serializable{
    ArrayList<String> headList;
    public HeadN(){
        //必须要new一个，否则会有空指针异常
        headList = new ArrayList<>();
    }
}
class Methods{
    //获得字符串的SHA-1值
    public static String calSHA1(String input) {
        try {
            //创建MessageDigest对象用于获得SHA1值
            MessageDigest messageDigest =MessageDigest.getInstance("SHA1");
            //把目标字符串转化为字节数组
            byte[] inputByteArray = input.getBytes();
            //将字节数组放入刚刚创建的MessageDigest对象中
            messageDigest.update(inputByteArray);
            //调用digest方法获得SHA1值并放入结果的字节数组中
            byte[] resultByteArray = messageDigest.digest();
            //将这个结果的字节数组转为16进制表示的字符串，即得到目标字符串的SHA1值
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    //把字节数组转化为16进制字符串
    public static String byteArrayToHex(byte[] byteArray) {
        //首先把全部16进制数放进一个字符数组中，方便后续使用
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
        char[] resultCharArray =new char[byteArray.length * 2];
        int index = 0;
        //使用增强for循环，遍历传入的字节数组
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }
        return new String(resultCharArray);
    }
    //获取字符串的长度
    public static int calSizeLen(String s){
        //将字符串转换为字节数组后，可直接调用数组的.length方法得到字符串的长度
        byte[] res = s.getBytes();
        return res.length;
    }
    //根据文件地址，获取文件内容,返回一个字符串String
    public static String getFileContent(String filePath) {
        String result = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            //只要没有读到文件末尾，即line=null，就循环读入
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            System.out.println("获取文件内容失败，请重新获取。");
            throw new RuntimeException(e+e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    //根据文件地址与想要写入的内容，将内容写入该文件
    public static void writeFileContent(String filePath,String content){
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //获得当前时间
    public static String getTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        return dateFormat.format(date);
    }

    //从socket中获得数据流，即接收objects文件夹中的文件
    //传入的参数为接收方的新建的objects文件夹地址以及连接用的接收方的socket
    public static void receiveFile(String recObjPath,Socket recSocket){
        //服务器成功接收请求后，成功建立连接，即可开始通信
        ObjectInputStream objectInputStream = null;
        InputStream inputStream;
        //读socket中传输过来的数据
        //首先通过socket获得输入字节流
        try {
            inputStream = recSocket.getInputStream();
            //读取该输入流,并将其反序列化得到一个Object类型的对象
            objectInputStream = new ObjectInputStream(inputStream);
            Object obj = objectInputStream.readObject();
            //将这个对象序列化进入接受方的objects文件夹
            git.WriteToObj(obj,recObjPath);
        } catch (IOException e) {
            throw new RuntimeException(e+e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e+e.getMessage());
        }
        //关闭连接，释放资源
        try {
            recSocket.close();
            inputStream.close();
            objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //传输数据流进入socket中，即传送文件
    //参数为传送的文件地址、传送方的socket
    public static void transmitFile(String transFile,Socket transSocket){
        //首先反序列化该文件
        Object obj = git.getClass(transFile);
        //然后传送该Object对象进入socket输出流
        OutputStream outputStream;
        ObjectOutputStream objectOutputStream;
        try {
            outputStream = transSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(obj);
            //服务端发送完消息后，需要给客户端一个标识
            //告诉客户端，我已经发送完成了，客户端就可以将接受的消息打印出来
            objectOutputStream.flush();
            transSocket.shutdownOutput();
        } catch (IOException e) {
            System.out.println("出现错误，错误为："+e.getMessage());
            System.out.println("发送数据失败，请重新发送");
            throw new RuntimeException(e);
        }
        try {
            transSocket.close();
            outputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e+e.getMessage());
        }
    }
}