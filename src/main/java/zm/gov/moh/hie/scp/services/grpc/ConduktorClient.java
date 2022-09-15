package zm.gov.moh.hie.scp.services.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Terence
 * The service implements streams to demonstration java ConnectionBuilder configuration
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConduktorClient implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = Logger.getLogger(ConduktorClient.class.getName());

     private ConduktorGrpc.ConduktorBlockingStub blockingStub;
    private ConduktorGrpc.ConduktorStub asyncStub;
    private ManagedChannel channel;

    private Data.FacilityID fd = Data.FacilityID.newBuilder()
            .setHmisCode("80060016")
            .setUuid("9e6ca02c-5166-4296-a905-08bb01a8b511")
            .build();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress("102.23.123.74", 55012)
                .keepAliveWithoutCalls(true)
                .keepAliveTime(5, TimeUnit.MINUTES)
                .keepAliveTimeout(2, TimeUnit.MINUTES)
                .usePlaintext();

        channel = channelBuilder.build();
         blockingStub = ConduktorGrpc.newBlockingStub(channel);
        asyncStub = ConduktorGrpc.newStub(channel);

        streamPatients();
        streamDispensations();
        streamPrescriptions();
    }

    private void streamPatients() {
        logger.info("Streaming patient profiles...");
        StreamObserver<Data.Patient> patientStreamObserver = new StreamObserver<>() {
            @Override
            public void onNext(Data.Patient value) {
                logger.info("=== Received patient ===");
                logger.info(value.getFirstName() + " " + value.getLastName());
            }

            @Override
            public void onError(Throwable t) {
                // This will fire up when a clean connection failure occurs
                // you need to figure out what you want to do next
                logger.warning("Connection fail");
            }

            @Override
            public void onCompleted() {
            }
        };
        asyncStub.streamPatientRegistrations(fd, patientStreamObserver);
    }

    private void streamDispensations() {
        logger.info("Streaming dispensations...");
        StreamObserver<Data.Dispensation> dispensationStreamObserver = new StreamObserver<>() {
            @Override
            public void onNext(Data.Dispensation value) {
                logger.info("=== Received dispensation ===");
                logger.info(value.getDispensationDate());
            }

            @Override
            public void onError(Throwable t) {
                // This will fire up when a clean connection failure occurs
                // you need to figure out what you want to do next
                logger.warning("Connection fail");
            }

            @Override
            public void onCompleted() {
            }
        };
        asyncStub.streamDispensations(fd, dispensationStreamObserver);
    }

    private void streamPrescriptions() {
        logger.info("Streaming prescriptions...");
        StreamObserver<Data.PrescriptionDetail> prescriptionStreamObserver = new StreamObserver<>() {
            @Override
            public void onNext(Data.PrescriptionDetail value) {
                logger.info("=== Received prescription ===");
                logger.info(value.getPatientUuid());
            }

            @Override
            public void onError(Throwable t) {
                // This will fire up when a clean connection failure occurs
                // you need to figure out what you want to do next
                logger.warning("Connection fail");
            }

            @Override
            public void onCompleted() {
            }
        };
        asyncStub.streamPrescriptions(fd, prescriptionStreamObserver);
    }

    @PreDestroy
    public void cleanUp() {
        try {
            channel.shutdownNow();
        } catch (Exception e) {
        }
    }
}
