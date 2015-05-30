package lfm;

import java.awt.Component;
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
	private static double[][] moc=null;
	public static void main(String [] args){
		lfm();
	}
	public static double[][] getMOC(){
		return moc;
	}
	public static void lfm(){
		//��������ڵ��ڽӾ���
		try{
			FileReader in=new FileReader("./football.txt");
			BufferedReader br=new BufferedReader(in);
			AdjacentMatrix=Matrix.read(br);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//�������нڵ�Ķȡ��ھӽڵ�
		HashMap nodesDegree=new HashMap<Integer,Double>();
		ArrayList neighbors=new ArrayList();
		ArrayList nodeNeighbors=new ArrayList();
		int sum=0;
		for(int i=0;i<AdjacentMatrix.getRowDimension();i++){
			for(int j=0;j<AdjacentMatrix.getColumnDimension();j++){
				if(AdjacentMatrix.get(i, j)==1){
					sum+=1;
					nodeNeighbors.add(j);
				}
			}
			nodesDegree.put(i, sum);
			neighbors.add(nodeNeighbors.clone());
			nodeNeighbors.clear();
			sum=0;
		}
		//����ڵ����Ҫ�̶�NI���ڵ����ϵ�����Խڵ����
		for(int m=0;m<neighbors.size();m++){
			ArrayList temp=(ArrayList) neighbors.get(m);
			int tempsum=0;
			for(int x=0;x<temp.size();x++){
				for(int y=x+1;y<temp.size();y++){
					if(AdjacentMatrix.get(Integer.parseInt(temp.get(x).toString()), Integer.parseInt(temp.get(y).toString()))==1){
						tempsum+=1;
					}
				}
			}
			nodesDegree.replace(m, tempsum/Double.parseDouble(nodesDegree.get(m).toString()));
		}
		//ʹ��collection.sort()�ͱȽϹ���Խڵ�����򣬴Ӵ�С��
		//public int compare(Object obj1, Object obj2)
		//�����������ķ���ֵ>0�����obj1����obj2ǰ��
		//<0�����obj1����obj2���
		//=0�����˳�򲻱�
		ArrayList<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(nodesDegree.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {   
		    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
		    	if(o2.getValue()>o1.getValue()){
		    		return 1;
		    	}
		    	else{
		    		return -1;
		    	}
		       // return (o2.getValue() - o1.getValue()); 
		        //return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//System.out.print(infoIds.get(1).getValue());
		
		/*for(int x=0;x<infoIds.size();x++){
			Map.Entry<String, Integer> temp=infoIds.get(x);
			int y=(int) (Math.random()*infoIds.size());
			infoIds.set(x, infoIds.get(y));
			infoIds.set(y, temp);
		}*/
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
		while(queue.pee][ [[[[[[[[[k()!=null);*/
		ArrayList clusters=new ArrayList<ArrayList<Integer>>();//������������
		//ArrayList members=new ArrayList();//���subgraph��Ա
		ArrayList<Integer>adjacentNodes=new ArrayList<Integer>();
		ArrayList group=new ArrayList<Integer>();
		double max=0;
		double temp1=0;
		double temp2=0;
		int maxNode=0;
		double a=1.0;
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
				temp1=fitnessFunction(group,a);
				group.remove(adjacentNodes.get(0));
				temp2=fitnessFunction(group,a);
				max=temp1-temp2;
				maxNode=(int) adjacentNodes.get(0);
				for(int j=1;j<adjacentNodes.size();j++){
					group.add(adjacentNodes.get(j));
					temp1=fitnessFunction(group,a);
					group.remove(adjacentNodes.get(j));
					temp2=fitnessFunction(group,a);
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
	   System.out.println(clusters);
	   //��������Ⱦ���
	   moc=new double[AdjacentMatrix.getRowDimension()][clusters.size()];
	   for(int m=0;m<clusters.size();m++){
		   ArrayList temp=(ArrayList) clusters.get(m);
		   for(int n=0;n<temp.size();n++){
			   moc[(int) temp.get(n)][m]=1;
		   }
	   }
	   double tempsum=0;
	   Matrix output=new Matrix(moc);
	   System.out.println("LFM��������Ч����"+EQ(AdjacentMatrix,output));
	  // Q(AdjacentMatrix,output);
	   for(int x=0;x<output.getRowDimension();x++){
		   for(int y=0;y<output.getColumnDimension();y++){
			   if(output.get(x, y)==1){
				   System.out.print(y+",");
			   }
		   }
		   System.out.println();
	   }
	   /*for(int x=0;x<AdjacentMatrix.getRowDimension();x++){
		   for(int y=0;y<clusters.size();y++){
			   if(moc[x][y]==1){
				   tempsum+=1;
			   }
		   }
		   for(int z=0;z<clusters.size();z++){
			   if(moc[x][z]==1){
				   moc[x][z]=1/tempsum;
			   }
		   }
		   tempsum=0;
	   }

	   //��ӡ�����Ⱦ���
	   output.print(1, 5);*/

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
	//�������Ч��
		public static double EQ(Matrix Adjacent,Matrix MOC){
			int nodeNum=0;
			int vclusterNum=0;
			int wclusterNum=0;
			int vdegree=0;
			int wdegree=0;
			double sum=0;
			int edgeNum=0;
			ArrayList al=new ArrayList();
			for(int x=0;x<Adjacent.getRowDimension();x++){
				for(int y=0;y<Adjacent.getColumnDimension();y++){
					edgeNum+=Adjacent.get(x,y);
				}
			}
			edgeNum/=2;
			for(int i=0;i<MOC.getColumnDimension();i++){
				//���������ڲ��ڵ�
				for(int j=0;j<MOC.getRowDimension();j++){
					if(MOC.get(j, i)==1){
						nodeNum+=1;
						al.add(j);
					}	
				}
				for(int k=0;k<nodeNum;k++){
					for(int l=0;l<nodeNum;l++){
						//����ڵ�v��w�����������ĸ���
						for(int m=0;m<MOC.getColumnDimension();m++){
							vclusterNum+=(int)MOC.get((int) al.get(k), m);
							wclusterNum+=(int)MOC.get((int)al.get(l), m);
						}
						//����ڵ�v��w�Ķ�
						for(int n=0;n<Adjacent.getColumnDimension();n++){
							vdegree+=Adjacent.get((int)al.get(k),n);
							wdegree+=Adjacent.get((int)al.get(l),n);
						}
						sum+=(1/((double)vclusterNum*wclusterNum))*(Adjacent.get((int) al.get(k), (int)al.get(l))-vdegree*wdegree/((double)2*edgeNum));
						vclusterNum=0;wclusterNum=0;vdegree=0;wdegree=0;
						//System.out.println("��ǰֵΪ��"+sum);
					}
				}
				nodeNum=0;
			}
			return sum/(2*edgeNum);
		}
	public static void Q(Matrix adjacent,Matrix MOC){
		double e[][]=new double[MOC.getColumnDimension()][MOC.getColumnDimension()];
		ArrayList clusters=new ArrayList();
		ArrayList cluster=new ArrayList();
		int sum=0;
		int ai=0;
		double Q=0;
		for(int i=0;i<MOC.getColumnDimension();i++){
			for(int j=0;j<MOC.getRowDimension();j++){
				if(MOC.get(j,i)==1){
					cluster.add(j);
				}
			}
			clusters.add(cluster.clone());
			cluster.clear();
		}
		for(int x=0;x<clusters.size();x++){
			for(int y=0;y<clusters.size();y++){
				ArrayList temp1=(ArrayList) clusters.get(x);
				ArrayList temp2=(ArrayList) clusters.get(y);
				for(int m=0;m<temp1.size();m++){
					for(int n=0;n<temp2.size();n++){
						if(adjacent.get((int)temp1.get(m),(int)temp2.get(n))==1){
							sum+=1;
						}
					}
				}
				e[x][y]=sum/2.0/613;
				sum=0;temp1.clear();temp2.clear();
			}
		}
		for(int m=0;m<e.length;m++){
			for(int n=0;n<e.length;n++){
				ai+=e[m][n];
			}
			Q+=e[m][m]-Math.pow(ai, 2);
			ai=0;
		}
		System.out.println("ģ���Q:"+Q);
	}
}
