public class com.serviceB.ServiceImpl {
  public static void ServerMain(String[] args) {
    ZMQServer<double[],Double> serv = new ZMQServer("com.serviceB.ServiceImpl","service.b");
    ExecutorService executor = new Executors.newFixedThreadPool(2);
    executor.submit(serv);
  }
}
