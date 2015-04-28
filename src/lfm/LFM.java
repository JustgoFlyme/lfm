package lfm;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import Jama.Matrix;

public class LFM {
	private static Matrix AdjacentMatrix;
	public static void main(String [] args){
		lfm();
	}
	public static void lfm(){
		//读入网络节点邻接矩阵
		try{
			FileReader in=new FileReader("./test.txt");
			BufferedReader br=new BufferedReader(in);
			AdjacentMatrix=Matrix.read(br);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//计算所有节点的度
		HashMap nodesDegree=new HashMap();
		int sum=0;
		for(int i=0;i<AdjacentMatrix.getRowDimension();i++){
			for(int j=0;j<AdjacentMatrix.getColumnDimension();j++){
				if(AdjacentMatrix.get(i, j)==1){
					sum+=1;
				}
			}
			nodesDegree.put(i, sum);
			sum=0;
		}
		//使用collection.sort()和比较规则对节点度排序，从大到小；
		//public int compare(Object obj1, Object obj2)
		//如果这个方法的返回值>0则代表obj1排在obj2前边
		//<0则代表obj1排在obj2后边
		//=0则代表顺序不变
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(nodesDegree.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
		        //return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//System.out.print(infoIds.get(1).getValue());
		//形成队列
		Queue  queue=new LinkedList();
		for(int i=0;i<infoIds.size();i++){
			queue.offer(infoIds.get(i).getKey());
		}
		/*
		 * 输出队列
		do{
			System.out.println(queue.poll().getValue());
		}
		while(queue.peek()!=null);*/
		ArrayList clusters=new ArrayList();//存放网络聚类结果
		//ArrayList members=new ArrayList();//存放subgraph成员
		ArrayList adjacentNodes=new ArrayList();
		ArrayList group=new ArrayList();
		double max=0;
		double temp1=0;
		double temp2=0;
		int maxNode=0;
		while(queue.peek()!=null){
			group.add(queue.poll());
			while(true){
				//获取初始社区的邻居节点
				for(int m=0;m<group.size();m++){
					for(int i=0;i<AdjacentMatrix.getColumnDimension();i++){
						if((AdjacentMatrix.get((int) group.get(m), i)==1)&&(!adjacentNodes.contains(i))){
							adjacentNodes.add(i);
						}
					}
				}
				//节点测度最大
				group.add(adjacentNodes.get(0));
				temp1=fitnessFunction(group,1.0);
				group.remove(adjacentNodes.get(0));
				temp2=fitnessFunction(group,1.0);
				max=temp1-temp2;
				maxNode=(int) adjacentNodes.get(0);
				for(int j=1;j<adjacentNodes.size();j++){
					group.add(adjacentNodes.get(j));
					temp1=fitnessFunction(group,1.0);
					group.remove(adjacentNodes.get(0));
					temp2=fitnessFunction(group,1.0);
					if(temp1-temp2>max){
						max=temp1-temp2;
						maxNode=(int) adjacentNodes.get(j);
					}
				}
				//将节点适应度最大的节点加入社区，并且从队列中移除该节点
				if(max>0){
					group.add(maxNode);
					queue.remove(maxNode);
				}
				else{
					break;
				}
			}
			clusters.add(group);
			group.clear();
		}
		for(Object q:clusters){
			System.out.println(q);
		}
	}
	//计算适应度函数，a为正实数
	public static double fitnessFunction(ArrayList Nodes,double a){
		int Kin=0;
		int Kout=0;
		ArrayList restNodes=new ArrayList();
		for(int x=0;x<AdjacentMatrix.getRowDimension();x++){
			restNodes.add(x);
		}
		for(int y=0;y<Nodes.size();y++){
			if(restNodes.contains(Nodes.get(y))){
				restNodes.remove(Nodes.get(y));
			}
		}
		for(int i=0;i<Nodes.size();i++){
			for(int j=i+1;j<Nodes.size();j++){
				if(AdjacentMatrix.get(Integer.parseInt(Nodes.get(i).toString()), Integer.parseInt(Nodes.get(j).toString()))==1){
					Kin+=1;
				}
			}
			for(int k=i+1;k<restNodes.size();k++){
				if(AdjacentMatrix.get(Integer.parseInt(Nodes.get(i).toString()), Integer.parseInt(restNodes.get(k).toString()))==1){
					Kout+=1;
				}
			}
		}
		restNodes.clear();	
		return 2*Kin/(Math.pow(2*Kin+Kout, a));
	}
}
