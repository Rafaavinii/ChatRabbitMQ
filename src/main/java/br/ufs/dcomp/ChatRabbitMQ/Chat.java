package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Chat {
  
  public static String destino = "";
  public static void main(String[] argv) throws Exception {
    
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("18.204.20.47	"); 
      factory.setUsername("admin");
      factory.setPassword("password"); 
      factory.setVirtualHost("/");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      
      Scanner input = new Scanner(System.in);
      
      String msg = ""; 

      String QUEUE_Send = "";
    
      String Exchange = "";


      System.out.print("usuario: "); 
      String QUEUE_NAME = input.nextLine();
      QUEUE_NAME = "@" + QUEUE_NAME;
      
      //canal para envio de mensagens
      channel.queueDeclare(QUEUE_NAME, false, false, false, null); 
      
      //consumidores de mensagen
      Consumer consumer_msg = new DefaultConsumer(channel) { 
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
          String message = new String(body, StandardCharsets.UTF_8);
          String sender = envelope.getRoutingKey();
          SimpleDateFormat sdf = new SimpleDateFormat("(dd/MM/yyyy HH:mm)");
          System.out.println(sdf.format(new Date()) + " " + sender + " diz: " + message);
          System.out.print(">> ");
        }
      }; 
      
      channel.basicConsume(QUEUE_NAME,true, consumer_msg);
      
      System.out.print(">>");
      msg = input.nextLine();
      
      while (!msg.equals("exit")) {
        String[] msg_par = msg.split(" ");

        if (msg.subSequence(0, 1).equals("@")){
          QUEUE_Send = msg;
          destino = QUEUE_Send;
          Exchange = "";
          
        }
        
        else if(msg.subSequence(0, 1).equals("#")){
          Exchange = msg.substring(1,msg.length());
          destino = msg;
          QUEUE_Send = "";
        }
        
        System.out.print(destino + ">>");
        msg = input.nextLine();
      }
      
      channel.close();
      connection.close();  
      

  }
}
