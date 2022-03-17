package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ProducerRecord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Producer{
    private final DataOutputStream outputStream;
    private final Socket broker;

    public Producer(Node brokerNode) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.broker = broker;
            this.outputStream = new DataOutputStream(broker.getOutputStream());
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void send(String topic, byte[] data) throws IOException {
        System.out.println("\nSending to broker, Topic: " + topic);
        ProducerRecord.ProducerMessage msg = ProducerRecord.ProducerMessage.newBuilder().setTopic(topic).setData(ByteString.copyFrom(data)).build();
        Any packet = Any.pack(msg);
        this.outputStream.write(packet.toByteArray());
    }

    public void close() throws IOException {
        broker.close();
    }
}