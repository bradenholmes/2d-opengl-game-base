package com.autopoker.main.graphics.renderer.renderable.utils;

public class IndexArrayUtils
{
	
	
	// ------------------------------------------------------------------------------------
	// QUAD    QUAD    QUAD    QUAD    QUAD    QUAD    QUAD    QUAD    QUAD    QUAD    QUAD
	// ------------------------------------------------------------------------------------
	/**
	 * Takes the max number of quads and creates an index array
	 * of that size.
	 * @param MAX_QUADS the maximum number of quads for this renderable
	 * @return an int[] of indices to send to the GPU
	 */
	public static int[] generateIndicesForQuads(int MAX_QUADS) {
		int[] elements = new int[6 * MAX_QUADS];
		for (int i = 0; i < MAX_QUADS; i++) {
			loadElementIndicesForQuad(elements, i);
		}
		
		return elements;
	}
	
	//Load indices for this specific quad
	private static void loadElementIndicesForQuad(int[] elements, int index) {
		int offsetArrayIndex = 6 * index;
		int offset = 4 * index;
		
		elements[offsetArrayIndex] = offset + 3;
		elements[offsetArrayIndex + 1] = offset + 2;
		elements[offsetArrayIndex + 2] = offset + 0;
		
		elements[offsetArrayIndex + 3] = offset + 0;
		elements[offsetArrayIndex + 4] = offset + 2;
		elements[offsetArrayIndex + 5] = offset + 1;
	}
	
	
	
	
	
	// --------------------------------------------------------------------------------------
	// BLOCK    BLOCK    BLOCK    BLOCK    BLOCK    BLOCK    BLOCK    BLOCK    BLOCK    BLOCK
	// --------------------------------------------------------------------------------------
	/**
	 * Takes the max number of blocks and creates an index array
	 * of that size.
	 * @param MAX_BLOCKS the maximum number of blocks for this renderable
	 * @return an int[] of indices to send to the GPU
	 */
	public static int[] generateIndicesForBlocks(int MAX_BLOCKS) {
		int[] elements = new int[36 * MAX_BLOCKS];
		for (int i = 0; i < MAX_BLOCKS; i++) {
			loadElementIndicesForBlock(elements, i);
		}
		
		return elements;
	}
	
	//Load indices for this specific block
	private static void loadElementIndicesForBlock(int[] elements, int index) {
		int offsetArrayIndex = 36 * index;
		int offset = 8 * index;
		
		//1
		elements[offsetArrayIndex] = offset + 2;
		elements[offsetArrayIndex + 1] = offset + 3;
		elements[offsetArrayIndex + 2] = offset + 7;
		//2
		elements[offsetArrayIndex + 3] = offset + 5;
		elements[offsetArrayIndex + 4] = offset + 2;
		elements[offsetArrayIndex + 5] = offset + 6;
		//3
		elements[offsetArrayIndex + 6] = offset + 0;
		elements[offsetArrayIndex + 7] = offset + 2;
		elements[offsetArrayIndex + 8] = offset + 1;
		//4
		elements[offsetArrayIndex + 9] = offset + 5;
		elements[offsetArrayIndex + 10] = offset + 1;
		elements[offsetArrayIndex + 11] = offset + 2;
		//5
		elements[offsetArrayIndex + 12] = offset + 2;
		elements[offsetArrayIndex + 13] = offset + 7;
		elements[offsetArrayIndex + 14] = offset + 6;
		//6
		elements[offsetArrayIndex + 15] = offset + 0;
		elements[offsetArrayIndex + 16] = offset + 3;
		elements[offsetArrayIndex + 17] = offset + 2;
		//7
		elements[offsetArrayIndex + 18] = offset + 7;
		elements[offsetArrayIndex + 19] = offset + 3;
		elements[offsetArrayIndex + 20] = offset + 0;
		//8
		elements[offsetArrayIndex + 21] = offset + 4;
		elements[offsetArrayIndex + 22] = offset + 1;
		elements[offsetArrayIndex + 23] = offset + 5;
		//9
		elements[offsetArrayIndex + 24] = offset + 1;
		elements[offsetArrayIndex + 25] = offset + 4;
		elements[offsetArrayIndex + 26] = offset + 0;
		//10
		elements[offsetArrayIndex + 27] = offset + 4;
		elements[offsetArrayIndex + 28] = offset + 5;
		elements[offsetArrayIndex + 29] = offset + 6;
		//11
		elements[offsetArrayIndex + 30] = offset + 4;
		elements[offsetArrayIndex + 31] = offset + 6;
		elements[offsetArrayIndex + 32] = offset + 7;
		//12
		elements[offsetArrayIndex + 33] = offset + 4;
		elements[offsetArrayIndex + 34] = offset + 7;
		elements[offsetArrayIndex + 35] = offset + 0;
	}
}
