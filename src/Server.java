import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
//
public class Server {

public static void main(String args[]) {

                try {
                    Scanner inn = new Scanner(System.in);
                    System.out.print("Введите абсолютный путь к папке: ");
                    String path = inn.nextLine();
                    ServerSocket providerSocket;
                    Socket connection = null;
                    ObjectOutputStream out;
                    ObjectInputStream in;
                    String message;
                    //1. creating a server socket
                    providerSocket = new ServerSocket(5000, 10);
                    //2. Wait for connection
                    System.out.println("Waiting for connection");
                    connection = providerSocket.accept();
                    System.out.println("Connection received from " + connection.getInetAddress().getHostName());
                    //3. get Input and Output streams
                    out = new ObjectOutputStream(connection.getOutputStream());
                    out.flush();
                    in = new ObjectInputStream(connection.getInputStream());

                    //Получаем список имен файлов в директории
                    ArrayList<String> arrayList=new ArrayList<String>();
                    File folder = new File(path);
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++)
                        arrayList.add(listOfFiles[i].getName());
                     //4. The two parts communicate via the input and output streams
                    //Передаем список имен Клиенту
                    out.writeObject(arrayList);
                    out.flush();

                    //Получаем имя
                        message = (String) in.readObject();

                        System.out.println("client>" + message);


                        //The InetAddress specification
                        InetAddress IA = InetAddress.getByName("localhost");

                        //Specify the file
                        File file = new File(path+"/"+ message);
                        out.writeObject(file.length());
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        //Get socket's output stream
                        OutputStream os = connection.getOutputStream();

                        //Read File Contents into contents array
                        byte[] contents;
                        long fileLength = file.length();
                        long current = 0;

                        long start = System.nanoTime();
                        while (current != fileLength) {
                            int size = 10000;
                            if (fileLength - current >= size)
                                current += size;
                            else {
                                size = (int) (fileLength - current);
                                current = fileLength;
                            }
                            contents = new byte[size];
                            bis.read(contents, 0, size);
                            os.write(contents);
                            System.out.println("Sending file ... " + (current * 100) / fileLength + "% complete!");
                        }
                        os.flush();
                        //File transfer done. Close the socket connection!

                        System.out.println("File sent succesfully!");



                        connection.close();
                    in.close();
                    out.close();
                }catch (IOException ex){
                    System.out.printf(ex.getMessage());
                }
                catch (ClassNotFoundException ex){
                    System.out.printf(ex.getMessage());
                }

        }
    }
