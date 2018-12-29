package com.baifenjy.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baifenjy.io.Request;
import com.baifenjy.io.Response;
import com.baifenjy.service.TeacherServiceImpl;
import com.baifenjy.vo.Teacher;

public class TeacherServer
{
    private static  TeacherServiceImpl teacherService;
    static{
        teacherService = new TeacherServiceImpl();
    }
    boolean started = false;
    ServerSocket ss = null;
    Socket s = null;
    String request = null;
    String response = null;
    
    public TeacherServer(){
        fun();
    }

    private void fun()
    {
        ss = Server.getInstance().getServerSocket();
        started = true;
        try {
            while (started) {
                s = ss.accept();
                System.out.println("a client connected success");
                Client c = new Client(s);
                new Thread(c).start();
            }
        } catch (EOFException e) {
            System.out.println("client has closed.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    class Client implements Runnable {

        private Socket s;
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean connected = false;

        public Client(Socket s) {
            this.s = s;
            try {
                this.dis = new DataInputStream(s.getInputStream());
                this.dos = new DataOutputStream(s.getOutputStream());
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            try {
                while (connected) {
                    request = dis.readUTF();
                    response = request;
                    dos.writeUTF(response);
                    if (request.equals(Request.COMMIT_TEC)) {
                        
                        String teacherStr = dis.readUTF();
                        Teacher teacher = (Teacher) JSON.parse(teacherStr);

                        boolean saveFlag = teacherService.saveOrUpdate(teacher);
                        if (saveFlag) {
                            dos.writeUTF(Response.SUCCESS);
                        }else {
                            dos.writeUTF(Response.FAIL);
                        }

                    } else if (request.equals(Request.QUERY_TEC_BY_ORDERID)) {
                        String orderId = dis.readUTF();
                        List<Teacher> tecs = teacherService.queryByOrderId(orderId);
                        String tecsString = JSON.toJSONString(tecs);
                        dos.writeUTF(tecsString);
                    } else if(request.equals(Request.QUERY_TEC_BY_TEACHER_PHONE)){
                        String phone = dis.readUTF();
                        Teacher teacher = teacherService.queryByPhone(phone);
                        String teacherStr = JSON.toJSONString(teacher);
                        dos.writeUTF(teacherStr);
                    }
                }
            } catch (SocketException e) {
                System.out.println("һ����½���Ѿ��ر�....");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        
        }

    }
    
}
