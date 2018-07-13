public class com.service.ServiceImpl {
  public static void ServerMain(String[] args) {
    ZMQServer<double[],Double> serv = new ZMQServer("com.service.ServiceImpl","service.a");
    ExecutorService executor = new Executors.newFixedThreadPool(2);
    executor.submit(serv);
  }
}
