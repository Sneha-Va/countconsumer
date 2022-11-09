
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;
    public class Countryconsumer {
        public static void main(String[] args) {
            KafkaConsumer consumer;
            String topic = "country";
            String broker = "localhost:9092";
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", "test-group");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer(props);
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
                    String msg=record.value();
                    int count=0;
                    for(int i=0; i< msg.length();i++) {
                        if (msg.charAt(i) != ' ')
                            count++;
                    }

                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/countrydb", "root", "");
                            String sql = "INSERT INTO `country`( `message`, `count`) VALUES (?,?)";
                            PreparedStatement stmt = con.prepareStatement(sql);
                            stmt.setString(1, msg);
                            stmt.setInt(2,count);
                            stmt.executeUpdate();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }



