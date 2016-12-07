package net.floodlightcontroller.dnsfilter;

public class Trie {
    private int SIZE = 38;
    private TrieNode root;  //字典树的根
//    public static int counter=0;
    Trie() {  //初始化字典树
        root = new TrieNode();
        
    }  
  
    private class TrieNode {  //字典树节点
        private int num;//有多少单词通过这个节点,即节点字符出现的次数 
        private TrieNode[] son;// 所有的儿子节点
        private boolean isEnd;//是不是最后一个节点
//        private char val;// 节点的值 
        TrieNode() {  
            num = 1; 
            son = new TrieNode[SIZE];//SIZE等于38  
            isEnd = false;
 //           Trie.counter++;
           
        }  
    }  
  //建立字典树
    public void insert(String str) {  //在字典树中插入一个域名
        if (str == null || str.length() == 0) {  
            return;
        }
    	if(this.has(str)){ //判断插入的是否已存在
    		return;
    	}
        TrieNode node = root;  
        char[] letters=str.toLowerCase().toCharArray();  
        for (int i = str.length()-1; i >=0; i--) {//逆序插入
        	int pos=getindex(letters[i]);// 获取字符的索引       	
            if (node.son[pos] == null) {  
                node.son[pos] = new TrieNode();  
 //               node.son[pos].val = letters[i];  
            } else {  
                node.son[pos].num++; 
            }  
            node = node.son[pos];  
        }  
        node.isEnd = true;  
    }  
  
      
    // 在字典树中查找一个完全匹配的域名.  
    public boolean has(String str) {  
        if (str == null || str.length() == 0) {  
            return false;  
        }     
        TrieNode node = root;  
        char[] letters=str.toLowerCase().toCharArray();  
        for (int i = str.length()-1; i >=0; i--) {  
        	int pos=getindex(letters[i]);// 获取字符的索引 
            if (node.son[pos] != null) {  
                node = node.son[pos];  
            } else {  
                return false;  
            }  
        }  
        return node.isEnd;  
    }
    //删除一个域名
    public boolean remove(String str){
    	if (str == null || str.length() == 0 ) {  
            return false;  
        } 
    	if(!this.has(str)){
    		return false;
    	}
    	TrieNode node = root;  
        char[] letters=str.toLowerCase().toCharArray();  
        for (int i = str.length()-1; i >=0; i--) {  
        	int pos=getindex(letters[i]);// 获取字符的索引
        	node.son[pos].num--;        	
        	if(i==0){//将结尾标识设置为false，相当于删除该域名
        		node.son[pos].isEnd=false;
        	}        	
            if(node.son[pos].num==0){//该节点的子树已没有域名经过，可以剪支
            	node.son[pos]=null;
            	break;
            }           
            node = node.son[pos]; 
        }
        
		return true;
    	
    }
   
    public TrieNode getRoot(){  
        return this.root;  
    }
    public int getindex(char c){
       	int pos=0;
    	if(Character.isDigit(c))
    		pos=c-'0'+26;
    	else if(Character.isLowerCase(c))
    		pos = c - 'a';
    	else if (c  =='-')
    		pos=36;
    	else
    		pos=37;
    	return pos;
    }
    
    
}

