package BPlusTree;

import java.io.*;

public class Main {

    private static BufferedReader buf;
    private static BPlusTree tree = null;
    public static void main(String[] args) throws Exception{
        String s=System.getProperty("user.dir");
        System.out.println("현재 디렉토리는 "+s+" 입니다.");

        if(args.length<1){
            System.out.println("Error!!");
            return;
        }

        switch (args[0]){
            case "-c":
                if(args.length==3) {
                    int num = Integer.parseInt(args[2]);
                    create(args[1], num);
                    break;
                }
                else {
                    System.out.println("잘못 입력했습니다!!");
                    break;
                }

            case "-i":
                if(args.length==3) {
                    insert(args[1], args[2]);
                    break;
                }
                else {
                    System.out.println("잘못 입력했습니다!!");
                    break;
                }
            case "-d":
                if(args.length==3) {
                    delete(args[1], args[2]);
                    break;
                }
                else {
                    System.out.println("잘못 입력했습니다!!");
                    break;
                }
            case "-s":
                if(args.length==3) {
                    int key = Integer.parseInt(args[2]);
                    single_search(args[1], key);
                    break;
                }
                else {
                    System.out.println("잘못 입력했습니다!!");
                    break;
                }
            case "-r":
                if(args.length==4) {
                    int st = Integer.parseInt(args[2]);
                    int ed = Integer.parseInt(args[3]);
                    //System.out.println("???");
                    ranged_search(args[1], st, ed);
                    break;
                }
                else {
                    System.out.println("잘못 입력했습니다!!");
                    break;
                }
        }
    }


    public static void create(String fileName, int num){
        tree=new BPlusTree(num);
        System.out.println("Create success!!");
        saveTree(fileName,tree);
    }

    public static void insert(String filename,String dataname){
        getTree(filename);
       // System.out.print(tree.degree+"111111131351\n");
        try{
            buf=new BufferedReader(new FileReader(dataname));
            while(true){
                String line=buf.readLine();
                if(line==null){
                    break;
                }
                String l1=line.split(",")[0];
                String l2=line.split(",")[1];

                int key=Integer.parseInt(l1);
                int val=Integer.parseInt(l2);
               // System.out.println(key+" "+val);
                tree.insert(key,val);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Insert success");
        saveTree(filename,tree);
    }

    public static void delete(String filename,String dataname){
        getTree(filename);
        try{
            buf=new BufferedReader(new FileReader(dataname));
            while(true){
                String line=buf.readLine();
                if(line==null){
                    break;
                }
                int key=Integer.parseInt(line);

                tree.delete(key);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Delete success");
        saveTree(filename,tree);
    }

    public static void single_search(String filename,int key){
        getTree(filename);
        tree.search_key(key);
    }

    public static void ranged_search(String filename,int st,int ed){
        getTree(filename);
        tree.ranged_search(st,ed);
    }


    static void saveTree(String fileName, BPlusTree tree) {
       // System.out.println("savetree.tree"+tree.degree);
        FileOutputStream fos=null;
        ObjectOutputStream oos=null;
        BPlusTree result = null;
        FileInputStream fis=null;
        ObjectInputStream ois=null;
        try {
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(tree);
            if(fos!=null) fos.close();
            if(oos!=null) oos.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
       // System.out.println("savetree.tree"+tree.degree);
        return;
    }

    static void getTree(String fileName) {
        tree = null;
        FileInputStream fis=null;
        ObjectInputStream ois=null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);

            tree = (BPlusTree) ois.readObject();
            if(fis!=null) fis.close();
            if(ois!=null) ois.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
       // System.out.println("loadtree.tree"+result.degree);
    }
}