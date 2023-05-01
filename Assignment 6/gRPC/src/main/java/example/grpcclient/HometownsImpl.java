package example.grpcclient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

public class HometownsImpl extends HometownsGrpc.HometownsImplBase implements Serializable{

    public static String file = "hometowns.data";
    List<Hometown> hometowns;

    public HometownsImpl(){
        this.hometowns =  new ArrayList<>();
    }

    @Override
    public void read(Empty request, StreamObserver<HometownsReadResponse> responseObserver) {
        HometownsReadResponse.Builder builder = HometownsReadResponse.newBuilder()
                .setIsSuccess(true);
        if (hometowns.isEmpty()) {
            builder.setIsSuccess(false);
            builder.setError("No previous users yet!");
        } else {
            builder.addAllHometowns(hometowns);
        }
        HometownsReadResponse response = builder.build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void search(HometownsSearchRequest request, StreamObserver<HometownsReadResponse> responseObserver) {
        HometownsReadResponse.Builder builder = HometownsReadResponse.newBuilder()
                .setIsSuccess(true);
        String city = request.getCity();
        List<Hometown> filtered = new ArrayList<>();
        if (!hometowns.isEmpty()) {
            for (Hometown hometown : hometowns) {
                if (hometown.getCity().equals(city)) {
                    filtered.add(hometown);
                }
            }
            if (!filtered.isEmpty()){
                builder.addAllHometowns(filtered);
            } else {
                builder.setIsSuccess(false);
                builder.setError("Oops, No one was born in Vermont or " + city + "!");
            }
        } else {
            builder.setIsSuccess(false);
            builder.setError("Oops, No previous users yet!");
        }
        HometownsReadResponse response = builder.build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void write(HometownsWriteRequest request, StreamObserver<HometownsWriteResponse> responseObserver) {
        Hometown hometown = request.getHometown();
        String city = hometown.getCity();
        HometownsWriteResponse.Builder builder = HometownsWriteResponse.newBuilder()
                .setIsSuccess(false);
        if (city != null) {
            hometowns.add(hometown);
            builder.setIsSuccess(true);
        } else {
            builder.setError("Oops, Are you sure that is a real place?!");
        }

        //Send the response back to the client
        HometownsWriteResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        save();
    }

    public static HometownsImpl load(){
        HometownsImpl loadHometownsImpl;
        try(FileInputStream in = new FileInputStream(file); ObjectInputStream os = new ObjectInputStream(in)) {
            loadHometownsImpl = (HometownsImpl) os.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HometownsImpl();
        } return loadHometownsImpl;
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file); ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}