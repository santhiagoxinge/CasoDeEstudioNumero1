package modality;


public interface ModalityFactory {

    DonationModality getModality();


    CollectionBag createBag();


    TraceabilityLabel createLabel();


    PreservationProtocol createPreservationProtocol();
}
