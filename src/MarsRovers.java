/**
 * Mars Rover Solution
 * @author Sachin Joshi
 */

import java.io.*;
import java.util.*;
import javafx.util.Pair;

public class MarsRovers {
	
	/**
	 * Each entry corresponds to the information for a particular chunk of the image.
	 * Each entry consists of an id of the chunk as the key along with its pair of starting byte and ending byte.
	 * E.g.,
			image_chunks.get("AFDB4BFA") is a pair of (0, 2418).
			0 is the starting byte of the chunk and 2418 is the ending byte of the chunk.
	 */
	private static HashMap<String, Pair<Integer, Integer>> image_chunks;
	
	/**
	 * Each entry corresponds to the size of a particular chunk of the image.
	 * Each entry consists of an id of the chunk mapped to its size.
	 * E.g.,
			chunks_size.get("AFDB4BFA") is 2417.
			chunks_size.get("F2B20750") is 1057.
	 */
	private static HashMap<String, Integer> chunks_size;
	
	/**
	 * Each entry corresponds to the id of a particular chunk of the image.
	 * E.g.,
			chunks_id.get(0) is "052E56B7".
			chunks_id.get(1) is "164DD7FB".
	 */
	private static ArrayList<String> chunks_id;
	
	/**
	 * Stores the id of the starting chunk.	
	*/
	private static String start_chunk;
	
	/**
	 * Stores the id of the ending chunk.	
	*/
	private static String end_chunk;
	
	/**
	 * Stores the size of the entire image.	
	*/
	private static int image_size;
	
	/**
	 * List of chunk ids to download which cover the entire image in the smallest total byte size.
	*/
	private static ArrayList<String> shortest_path;
	
	/**
	 * Smallest total byte size
	*/
	private static int min_cost;
	
	/**
	* Constructor to initialize class variables
	*/
	MarsRovers() {
		image_chunks = new HashMap<String, Pair<Integer, Integer>>();
		chunks_size = new HashMap<String, Integer>();
		chunks_id = new ArrayList<String>();
		start_chunk = end_chunk = "";
		shortest_path = new ArrayList<String>();
		min_cost = 0;
	}
	
	/**
	 * This function reads the data from the .tsv test file.
	 * Fills up image_chunks, chunks_size and chunks_id corpus.
	 * Assigns values to variables such as image_size, start_chunk and end_chunk.
	 * end_byte = start_byte + size - 1. 
     * @param filename	 
	*/
	private static void readTestData(String filename)throws IOException {
		BufferedReader tsvfile = new BufferedReader(new FileReader(filename));
		String data;
		image_size = Integer.parseInt(tsvfile.readLine());
		
		while((data = tsvfile.readLine()) != null) {
			String arr_data[] = data.split("\t");
			int start_byte = Integer.parseInt(arr_data[1]);
			int size = Integer.parseInt(arr_data[2]);
			int end_byte = start_byte + size - 1;
			image_chunks.put(arr_data[0], new Pair<Integer, Integer>(start_byte, end_byte));
			chunks_size.put(arr_data[0], size);
			chunks_id.add(arr_data[0]);
				
			if(start_byte == 0) {
				start_chunk = arr_data[0];
			}
			if((start_byte + size) == image_size) {
				end_chunk = arr_data[0];
			}
		}
		tsvfile.close();
	}
	
	/**
	 * This function creates a directional graph given the image chunks.
	 * An edge exists between the chunks u -> v, if the end_byte of chunk u is greater than the start_byte of chunk v and less than the end_byte of chunk v, such that the two chunks overlap. 
	 * Represents the graph as an Adjacency List using BFS algorithm.
     * @param g: Object of the class Graph	 
	*/
	private static void createAdjacencyList(Graph g) {
		Queue<String> q = new LinkedList<String>();
		q.add(start_chunk);
		HashMap<String, Character> inqueue = new HashMap<String, Character>();
		inqueue.put(start_chunk, 'T');
		
 		while(!q.isEmpty()) {
			String temp = q.poll();
			int end_byte = image_chunks.get(temp).getValue();
			for(String id : chunks_id) {
				if((end_byte > image_chunks.get(id).getKey()) && (end_byte < image_chunks.get(id).getValue())) {
					ArrayList<String> edges = g.adjlist.get(temp);
					if(edges == null) {
						edges = new ArrayList<String>();
					}
					edges.add(id);
					g.adjlist.put(temp, edges);
					if(inqueue.get(id) == null) {
						q.add(id);
						inqueue.put(id, 'T');
					}
				}
			}			
		}
	}
	
	/**
	 * This function prints the graph as an Adjacency List. 
     * @param graph: Object of the class Graph	 
	*/
	private static void printGraph(Graph graph) {
		System.out.println(graph.adjlist);
	}
	
	/**
	 * This function initially assigns the value to the min_cost variable as the sum of the size of all the image chunks.
	 */
	private static void initializeMinimumCost() {
		for(String id : chunks_id) {
			min_cost = min_cost + chunks_size.get(id);
		}
	}
	
	/**
	 * This function computes the shortest path between the source chunk and destination chunk.
	 * Source chunk is the start_chunk and destination chunk is the end_chunk.
	 * Objective is to find the list of chunk ids that minimizes the cost.
	 * Calls the helper function DFS to calculate the path with the minimum cost. 
     * @param graph, src, dest	 
	*/
	private static void shortestPath(Graph graph, String src, String dest) {
		if(src.compareTo(dest) == 0) {
			shortest_path.add(src);
		}
		
		HashMap<String, Boolean> isVisited = new HashMap<String, Boolean>();
		for(String id : chunks_id) {
			isVisited.put(id, false);
		}
		
		Deque<String> pathList = new ArrayDeque<String>();
		int cost = 0;
        DFS(graph, src, dest, isVisited, pathList, cost);
    }
 
    /**
	 * This function implements the DFS algorithm.
	 * Computes all the paths between the start_chunk and the end_chunk using the recursive approach.
	 * Finds the path with the minimum cost and assigns it to shortest_path.
	 * shortest_path is the list of chunk ids that covers the entire image in the smallest total byte size. 
     * @param args
	 * graph: Object of the class Graph.
	 * src: Source Chunk.
	 * dest: Destination Chunk. 
	 * isVisited: List of chunks visited in the path. 
	 * pathList: A possible path from the source chunk to the destination chunk.
	 * cost: Cost associated with a particular path.	 
	*/
    private static void DFS(Graph graph, String src, String dest, HashMap<String, Boolean> isVisited, Deque<String> pathList, int cost) {
        isVisited.put(src, true);
		pathList.add(src);
        cost = cost + chunks_size.get(src);
		
        if (src.compareTo(dest) == 0) 
        {
			if(cost < min_cost) {
				min_cost = cost;
				shortest_path = new ArrayList<String>(pathList);
			}
        }
        else {
			if(graph.adjlist.containsKey(src)) {
				for(String vertex : graph.adjlist.get(src)) {
					if(!isVisited.get(vertex)) {
						DFS(graph, vertex, dest, isVisited, pathList, cost);
					}
				}
			}
		} 
        pathList.removeLast();
        isVisited.put(src, false);
    }
	
	/**
	 * main function to create the object of the Graph class.
	 * Call the above defined functions to achieve different functionality.
     * Prints the result to the STDOUT. 	 
	*/
	public static void main(String args[])throws IOException {
		MarsRovers mars = new MarsRovers();
		mars.readTestData("problem.tsv");
		Graph graph = new Graph();
		mars.createAdjacencyList(graph);
		mars.initializeMinimumCost();
		mars.shortestPath(graph, start_chunk, end_chunk);
		Collections.sort(shortest_path);
		for(String chunk : shortest_path) {
			System.out.println(chunk);
		}	
	}
}