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
		//��������ڵ��ڽӾ���
		try{
			FileReader in=new FileReader("./test.txt");
			BufferedReader br=new BufferedReader(in);
			AdjacentMatrix=Matrix.read(br);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//�������нڵ�Ķ�
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
		//ʹ��collection.sort()�ͱȽϹ���Խڵ�����򣬴Ӵ�С��
		//public int compare(Object obj1, Object obj2)
		//�����������ķ���ֵ>0�����obj1����obj2ǰ��
		//<0�����obj1����obj2���
		//=0�����˳�򲻱�
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(nodesDegree.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
		        //return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//System.out.print(infoIds.get(1).getValue());
		//�γɶ���
		Queue  queue=new LinkedList();
		for(int i=0;i<infoIds.size();i++){
			queue.offer(infoIds.get(i).getKey());
		}
		/*
		 * �������
		do{
			System.out.println(queue.poll().getValue());
		}
		while(queue.peek()!=null);*/
		ArrayList clusters=new ArrayList();//������������
		//ArrayList members=new ArrayList();//���subgraph��Ա
		ArrayList<Integer>adjacentNodes=new ArrayList<Integer>();
		ArrayList group=new ArrayList<Integer>();
		double max=0;
		double temp1=0;
		double temp2=0;
		int maxNode=0;
		while(queue.peek()!=null){
			group.add((Integer) queue.poll());
			while(true){
				//��ȡ��ʼ�������ھӽڵ�
				for(int m=0;m<group.size();m++){
					for(int i=0;i<AdjacentMatrix.getColumnDimension();i++){
						if((AdjacentMatrix.get((int) group.get(m), i)==1)&&(!adjacentNodes.contains(i))){
							adjacentNodes.add(i);
						}
					}
				}
				for(int n=0;n<group.size();n++){
					if(adjacentNodes.contains(group.get(n))){
						adjacentNodes.remove(group.get(n));
					}
					else{
						continue;
					}
				}
				//�ڵ������
				group.add(adjacentNodes.get(0));
				temp1=fitnessFunction(group,1.0);
				group.remove(adjacentNodes.get(0));
				temp2=fitnessFunction(group,1.0);
				max=temp1-temp2;
				maxNode=(int) adjacentNodes.get(0);
				for(int j=1;j<adjacentNodes.size();j++){
					group.add(adjacentNodes.get(j));
					temp1=fitnessFunction(group,1.0);
					group.remove(adjacentNodes.get(j));
					temp2=fitnessFunction(group,1.0);
					if(temp1-temp2>max){
						max=temp1-temp2;
						maxNode=(int) adjacentNodes.get(j);
					}
				}
				//���ڵ���Ӧ�����Ľڵ�������������ҴӶ������Ƴ��ýڵ�
				if(max>0){
					group.add(maxNode);
					queue.remove(maxNode);
					adjacentNodes.clear();
					max=0;
					maxNode=0;
					temp1=0;
					temp2=0;
				}
				else{
					break;
				}
			}
			clusters.add(group.clone());
			group.clear();
			adjacentNodes.clear();
			max=0;
			maxNode=0;
			temp1=0;
			temp2=0;
		}
	   System.out.print(clusters);

	}
	//������Ӧ�Ⱥ�����aΪ��ʵ��
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
			for(int k=0;k<restNodes.size();k++){
				if(AdjacentMatrix.get(Integer.parseInt(Nodes.get(i).toString()), Integer.parseInt(restNodes.get(k).toString()))==1){
					Kout+=1;
				}
			}
		}
		restNodes.clear();	
		return 2*Kin/(Math.pow(2*Kin+Kout, a));
	}
}
