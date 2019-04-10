package com.ndipiazza.clumsy;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClumsyServer {


  public static void main(String [] args) throws Exception {
    Server server = new Server(Integer.parseInt(args[0]));
    Thread thread = new Thread(() -> {
      try {
        server.setHandler(new AbstractHandler() {
          @Override
          public void handle(String target,
                             Request baseRequest,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            List<String> taskKillCommand = new ArrayList<>();
            taskKillCommand.add("taskkill");
            taskKillCommand.add("/F");
            taskKillCommand.add("/IM");
            taskKillCommand.add("clumsy.exe");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(taskKillCommand);
            Process killProc = processBuilder.start();
            try {
              killProc.waitFor(15000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            if (!target.contains("stop")) {
              List<String> clumsyCommand = new ArrayList<>();
              clumsyCommand.add("clumsy.exe");
              clumsyCommand.add("--filter");
              clumsyCommand.add("tcp and (tcp.DstPort == 443 or tcp.SrcPort == 443)");
              clumsyCommand.add("--drop");
              clumsyCommand.add("on");
              clumsyCommand.add("--drop-inbound");
              clumsyCommand.add("on");
              clumsyCommand.add("--drop-outbound");
              clumsyCommand.add("on");
              clumsyCommand.add("--drop-chance");
              clumsyCommand.add("100.0");
              processBuilder = new ProcessBuilder();
              processBuilder.command(clumsyCommand);
              processBuilder.start();
            }
          }
        });
        server.start();
        server.join();
      } catch (Exception e) {
        System.err.println("Couldn't create embedded jetty server");
        e.printStackTrace();
      }
    });
    thread.start();
    thread.join();
  }
}
