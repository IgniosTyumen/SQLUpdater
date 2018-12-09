public class Main {
    public static void main(String[] args) throws Exception {
        JDBCInitializer.connect();
//        ReadTarifsFromXLSM reader = new ReadTarifsFromXLSM();
//        reader.processOneSheet("AC.xlsx");
//        ReadTarifsFromXLSM reader2 = new ReadTarifsFromXLSM();
//        reader2.processOneSheet("AC3.xlsx");
        ReadTarifsFromXLSM reader3 = new ReadTarifsFromXLSM();
        reader3.processOneSheet("AC.xlsx");
        JDBCInitializer.close();
    }
}
