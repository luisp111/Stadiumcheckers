package edu.up.cs301.game.GameFramework.utilities;

public class Equality_Methods {

    public static <T> boolean arrayEquals(T[] arr1, T[] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(arr1[i] == null && arr2[i] == null){ continue; }
            if(!arr1[i].equals(arr2[i])){
                return false;
            }
        }
        return true;
    }

    public static boolean arrayEquals(int[] arr1, int[] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(arr1[i] != arr2[i]){
                return false;
            }
        }
        return true;
    }

    public static boolean arrayEquals(boolean[] arr1, boolean[] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(arr1[i] != arr2[i]){
                return false;
            }
        }
        return true;
    }

    public static <T> boolean doubleArrayEquals(T[][] arr1, T[][] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(!arrayEquals(arr1[i],arr2[i])){
                return false;
            }
        }
        return true;
    }

    public static boolean doubleArrayEquals(int[][] arr1, int[][] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(!arrayEquals(arr1[i],arr2[i])){
                return false;
            }
        }
        return true;
    }

    public static boolean doubleArrayEquals(boolean[][] arr1, boolean[][] arr2){
        if(arr1.length != arr2.length){
            return false;
        }
        for(int i = 0; i < arr1.length; i++){
            if(!arrayEquals(arr1[i],arr2[i])){
                return false;
            }
        }
        return true;
    }
}
