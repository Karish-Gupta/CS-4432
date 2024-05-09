import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Exit the program and give message if number of frames in not passed by user
        if (args.length < 1) {
            System.out.println("Please enter number of frames for buffer value when calling main\n");
            System.exit(1);
        }
        // Initialize
        int bufferPoolSize = Integer.parseInt(args[0]);
        BufferPool aBufferPool = new BufferPool(bufferPoolSize);

        System.out.println("Select Frame Eviction Algorithm (Circular) or (LRU)");
        Scanner algInput = new Scanner(System.in);
        String algSelection = algInput.nextLine();
        if (algSelection.equals("LRU")) {
            aBufferPool.LRUPolicy(true);
        }

            while(true) {
                System.out.println("The program is ready for the next command (Get, Set, Pin, Unpin)");
                Scanner userInput = new Scanner(System.in);
                String arguments = userInput.nextLine();
                String[] splitArguments = arguments.split(" ");
                String command = splitArguments[0];

                switch (command) {
                    case "Get":
                        aBufferPool.handleGet(Integer.parseInt(splitArguments[1]));
                        break;

                    case "Set":
                        // Extract the new content within double quotes
                        StringBuilder newContentBuilder = new StringBuilder();
                        boolean startedQuote = false;
                        for (int i = 2; i < splitArguments.length; i++) {
                            if (splitArguments[i].startsWith("\"")) {
                                // New content starts
                                newContentBuilder.append(splitArguments[i].substring(1));
                                startedQuote = true;
                            } else if (splitArguments[i].endsWith("\"")) {
                                // New content ends
                                newContentBuilder.append(" ").append(splitArguments[i], 0, splitArguments[i].length() - 1);
                                startedQuote = false;
                                break;
                            } else if (startedQuote) {
                                // Inside the quoted section
                                newContentBuilder.append(" ").append(splitArguments[i]);
                            }
                        }
                        String newContent = newContentBuilder.toString();

                        aBufferPool.handleSet(Integer.parseInt(splitArguments[1]), newContent);
                        break;

                    case "Pin":
                        aBufferPool.handlePinBid(Integer.parseInt(splitArguments[1]));
                        break;

                    case "Unpin":
                        aBufferPool.handleUnpinBid(Integer.parseInt(splitArguments[1]));
                        break;

                    default:
                        break;
                }
            }
        }
    }