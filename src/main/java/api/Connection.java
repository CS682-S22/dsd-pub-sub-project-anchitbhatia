package api;

import utils.ConnectionException;
import utils.Constants;
import utils.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/***
 * Connection class to manage connection
 * @author anchitbhatia
 */
public class Connection {
    private final Node node;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final BlockingQueue<byte[]> sendQueue;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.node = new Node(socket.getInetAddress().getHostName(), socket.getPort());
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        sendQueue = new LinkedBlockingDeque<>();
    }

    /***
     * Method to add record to subscriber's send queue
     * @param record to be sent to subscriber
     */
    public void addQueue(byte[] record){
        this.sendQueue.add(record);
    }

    /***
     * Method to poll subscriber's send queue
     * @return bytes read from queue
     */
    public byte[] pollSendQueue() {
        try {
            return this.sendQueue.poll(Constants.POLL_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    /***
     * Method to receive bytes
     * @return bytes read
     */
    public byte[] receive(){
        byte[] buffer = null;
        try {
            int length = this.inputStream.readInt();
            if (length > 0) {
                buffer = new byte[length];
                this.inputStream.readFully(buffer, 0, buffer.length);
            }
        } catch (EOFException ignored) {
        } //No more content available to read
        catch (SocketException exception) {
            return null;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return buffer;
    }

    /***
     * Method to send bytes
     * @param message : message to be sent
     */
    public void send(byte[] message) throws ConnectionException {
        try {
            if (!this.socket.isClosed()) {
                this.outputStream.writeInt(message.length);
                this.outputStream.write(message);
            }
        } catch (SocketException e) {
            throw new ConnectionException("Unable to send. Broken pipe.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Method to get port of the connection
     * @return port number of the connection
     */
    public int getPort(){
        return this.node.getPort();
    }

    /***
     * Method to check if connection is closed
     * @return true if connection is closed else false
     */
    public boolean isClosed(){
        return this.socket.isClosed();
    }

    /***
     * Method to close connection
     */
    public void close() throws IOException {
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }

    @Override
    public String toString() {
        return this.node.getHostName() + ":" + this.node.getPort();
    }
}
