import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Aufgabe2 {

    private int[][] rawBlocks;
    private List<BlockGroup> blocks = new ArrayList<>();
    private int[] boxDimensions;
    private int[][][] box;

    private Aufgabe2(String[] args){
        readInput(args);
        groupBlocks();
        blocks.forEach(System.out::println);
        initBox();
        System.out.println(Arrays.deepToString(box));
    }

    private void initBox(){
        for (int x = 0; x < boxDimensions[0]; x++) {
            for (int y = 0; y < boxDimensions[1]; y++) {
                for (int z = 0; z < boxDimensions[2]; z++) {
                    box[x][y][z] = -1;
                }
            }
        }
        box[boxDimensions[0] / 2][boxDimensions[1] / 2][boxDimensions[2] / 2] = -2;
    }

    private void groupBlocks(){
        for (int i = 0; i < rawBlocks.length; i++) {
            int[] sortedBlock = Arrays.stream(rawBlocks[i]).sorted().toArray();
            boolean isDuplicate = false;
            for (BlockGroup block : blocks) {
                if (Arrays.equals(block.orientations[0], sortedBlock)) {
                    isDuplicate = true;
                    block.blockIds.add(i);
                    break;
                }
            }
            if (!isDuplicate){
                blocks.add(new BlockGroup(getOrientations(sortedBlock), new ArrayList<>(Collections.singleton(i))));
            }
        }
    }

    //Read input using the passed arguments
    private void readInput(String[] args){
        if (args.length < 1){
            System.out.println("Syntax: Aufgabe3 <Pfad zur Eingabedatei>");
            System.exit(0);
        }
        File inputFile = new File(args[0]);
        if (!inputFile.exists()){
            System.out.println("Datei existiert nicht.");
            System.exit(0);
        }
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(Files.newInputStream(inputFile.toPath()), StandardCharsets.UTF_8))){
            boxDimensions = parseToIntArray(br.readLine());
            box = new int[boxDimensions[0]][boxDimensions[1]][boxDimensions[2]];
            int blockCount = parseToIntArray(br.readLine())[0];
            rawBlocks = new int[blockCount][];
            for (int i = 0; i < blockCount; i++) {
                rawBlocks[i] = parseToIntArray(br.readLine());
            }
        }
        catch (IOException e){
            System.out.println("Fehler beim Laden der Eingabedatei");
            System.exit(0);
        }
    }

    //Parse line, of numbers to and integer array
    private int[] parseToIntArray(String line){
        return Arrays.stream(line
                        .replaceAll("\\D+", "") //Remove unwanted characters (including BOM)
                        .split("(?!^)")) //Split without first empty element
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    //Get all possible orientations of a block
    private int[][] getOrientations(int[] orientation){
        List<int[]> out = new ArrayList<>();
        int length = orientation.length;
        int[] stack = new int[length];
        out.add(orientation.clone());
        int index = 1;
        while (index < length){
            if (stack[index] < index){
                boolean swapped = true;
                int temp;
                if ((index % 2) == 0 && orientation[0] != orientation[index]){
                    temp = orientation[0];
                    orientation[0] = orientation[index];
                    orientation[index] = temp;
                }
                else if (orientation[stack[index]] != orientation[index]){
                    temp = orientation[stack[index]];
                    orientation[stack[index]] = orientation[index];
                    orientation[index] = temp;
                }
                else{
                    swapped = false;
                }
                if (swapped && out.stream().noneMatch(e -> Arrays.equals(e, orientation))){
                    out.add(orientation.clone());
                }
                stack[index]++;
                index = 1;
            }
            else{
                stack[index] = 0;
                index++;
            }
        }
        return out.toArray(int[][]::new);
    }

    public static void main(String[] args) {
        new Aufgabe2(args);
    }

    private class BlockGroup{
        int[][] orientations;
        List<Integer> blockIds;

        public BlockGroup(int[][] orientations, List<Integer> blockIds) {
            this.orientations = orientations;
            this.blockIds = blockIds;
        }

        @Override
        public String toString() {
            return Arrays.deepToString(orientations) + "\n" + blockIds.toString();
        }
    }
}
