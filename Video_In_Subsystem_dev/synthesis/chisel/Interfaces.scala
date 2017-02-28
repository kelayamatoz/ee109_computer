import Chisel._

class BasicInterface extends Bundle {
  val reset = Bool(INPUT)
}

class StreamInInterface(dIn: Int) extends Bundle {
  val stream_in_startofpacket = Bool(INPUT) 
  val stream_in_endofpacket   = Bool(INPUT)
  val stream_in_valid         = Bool(INPUT)
  val stream_in_ready         = Bool(INPUT)
  val stream_in_data          = UInt(INPUT, width=dIn)

}

class StreamOutInterface(dOut: Int) extends Bundle {
  val stream_out_startofpacket = Bool(OUTPUT)
  val stream_out_endofpacket   = Bool(OUTPUT)
  val stream_out_valid         = Bool(OUTPUT)
  val stream_out_ready         = Bool(OUTPUT)
  val stream_out_data          = UInt(OUTPUT, width=dOut)
}

class DMAInterface(dir: Bool, slaveDataSize: Int, masterDataSize: Int, addrSize: Int) extends Bundle {
  if (dir) {
    // From stream to memory
    // For now, hard-code the slave interface info
    val slave_address      = UInt(INPUT, 2) 
    val slave_byteenable   = UInt(INPUT, 3) 

    val slave_read         = Bool(INPUT) 
    val slave_write        = Bool(INPUT) 
    val slave_writedata    = UInt(INPUT, slaveDataSize) 
    val slave_readdata     = UInt(OUTPUT, slaveDataSize) 
    val master_address     = UInt(OUTPUT, addrSize) 
    val master_waitrequest = Bool(INPUT)
    val master_write       = Bool(OUTPUT) 
    val master_writedata   = UInt(OUTPUT, masterDataSize) 
  } else {
    println("Not implemented") 
  }
}

// Interface for the decoder peripheral
class VideoDecoderInterface(dSize: Int) extends Bundle {
  // These are exports
  val TD_CLK27      = Bool(INPUT) 
  val TD_DATA       = UInt(INPUT, dSize) 
  val TD_HS         = Bool(INPUT) 
  val TD_VS         = Bool(INPUT)
  val clk27_reset   = Bool(INPUT) 
  val TD_RESET      = Bool(OUTPUT) 
  val overflow_flag = Bool(OUTPUT)
}


// Main object
class Video_In_Subsystem_dev() extends BlackBox {
  val PeripheralDataSize = 8
  val ChromaResamplerDataSize = 24
  val ClipperDataSize = 16
  val CSCDataSize = 24
  val DecoderDataSize = 16
  val RGBResamplerDataSize = 16
  val ScalerDataSize = 16 
  val DMASlaveDataSize = 32 
  val DMAMasterAddrSize = 32 
  val DMAMasterDataSize = 16

  val io = Bundle {
    val sys_reset_reset_n = Bool(INPUT)
    val inputPeriph = new VideoDecoderInterface(PeripheralDataSize) 
    val outputDMA = new DMAInterface(ScalerDataSize, )
  } 

  val decoder = new Video_In_Subsystem_dev_Video_In(PeripheralDataSize, DecoderDataSize)
  val chromaResampler = new Video_In_Subsystem_dev_Video_In_Chroma_Resampler(DecoderDataSize, ChromaResamplerDataSize)
  val csc = new Video_In_Subsystem_dev_Video_In_CSC(ChromaResamplerDataSize, CSCDataSize) 
  val rgbResampler = new Video_In_Subsystem_dev_Video_In_RGB_Resampler(CSCDataSize, RGBResamplerDataSize) 
  val clipper = new Video_In_Subsystem_dev_Video_In_Clipper(RGBResamplerDataSize, ClipperDataSize) 
  val scaler = new Video_In_Subsystem_dev_Video_In_Scaler(ClipperDataSize, ScalerDataSize) 
  val DMA = new Video_In_Subsystem_dev_Video_In_DMA(ScalerDataSize, true, DMASlaveDataSize, DMAMasterDataSize, DMAMasterAddrSize) 

  // Connections  
  // TODO: Needs to add reset controller   
  // decoder
  decoder.io.reset := io.sys_reset_reset_n 
  decoder.io.peripheral.TD_CLK27      := io.inputPeriph.TD_CLK27
  decoder.io.peripheral.TD_DATA       := io.inputPeriph.TD_DATA       
  decoder.io.peripheral.TD_HS         := io.inputPeriph.TD_HS        
  decoder.io.peripheral.TD_VS         := io.inputPeriph.TD_VS        
  decoder.io.peripheral.clk27_reset   := io.inputPeriph.clk27_reset  
  decoder.io.peripheral.TD_RESET      := io.inputPeriph.TD_RESET     
  decoder.io.peripheral.overflow_flag := io.inputPeriph.overflow_flag

  // chromaResampler
  chromaResampler.io.streamIn.stream_in_startofpacket  
  chromaResampler.io.streamIn.stream_in_endofpacket   
  chromaResampler.io.streamIn.stream_in_valid         
  chromaResampler.io.streamIn.stream_in_ready         
  chromaResampler.io.streamIn.stream_in_data          
                                                      
                                                      

}

// Interfaces for instances 
// TODO: Names of these classes need to be regenerated based on the name of the project
class Video_In_Subsystem_dev_Video_In(val inputDataSize: Int, val outputDataSize: Int)  extends BlackBox {
  val io = new BasicInterface {
    val peripheral = new VideoDecoderInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }  
}

class Video_In_Subsystem_dev_Video_In_Chroma_Resampler(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_CSC(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_RGB_Resampler(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_RGB_Resampler(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_Clipper(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_Scaler(val inputDataSize: Int, val outputDataSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val streamOut = new StreamOutInterface(outputDataSize)
  }
}

class Video_In_Subsystem_dev_Video_In_DMA(val inputDataSize: Int, val dir: Bool, val slaveDataSize: Int, val masterDataSize: Int, val addrSize: Int) extends BlackBox {
  val io = new BasicInterface {
    val streamIn = new StreamInInterface(inputDataSize) 
    val DMAOut = new DMAInterface(dir, slaveDataSize, masterDataSize, addrSize)
  }
}
