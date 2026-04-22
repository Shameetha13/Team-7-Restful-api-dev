package pojoclass;

public class ObjectAndCollection {
  private String name;
    private Data data;

    public static class Data {
        private String year;
        private String price;
        private String cpu;
        private String harddisk;

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getCpu() { return cpu; }
        public void setCpu(String cpu) { this.cpu = cpu; }

        public String getHarddisk() { return harddisk; }
        public void setHarddisk(String harddisk) { this.harddisk = harddisk; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
}
