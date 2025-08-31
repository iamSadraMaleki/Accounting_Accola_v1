package com.example.project.transection;

import java.util.List;

public interface TransferService {


    TransferDto recordTransfer(CreateTransferRequest request);


     List<TransferDto> getTransfersForCurrentUser();
}
