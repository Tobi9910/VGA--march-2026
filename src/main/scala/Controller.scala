import chisel3._

//

//needs:
//clock
//reset

//video_on
//hsync
//vsync
//p_tick
//output x (10 bit
//output y (10 bit

class vga_controller extends Module {
  val io = IO(new Bundle {
    val reset = Input(UInt(1.W))
    val video_on = Output(UInt(1.W))
    val hsync = Output(UInt(1.W))
    val vsync = Output(UInt(1.W))
    val p_tick = Output(UInt(2.W))
    val x = Output(UInt(10.W))
    val y = Output(UInt(10.W))
    
  })

  //HMAX = 799
  //VMAX = 524
  io.video_on := 0.U
  io.hsync := 0.U
  io.vsync := 0.U
  io.p_tick := 0.U
  io.x := 0.U
  io.y := 0.U


  //Frame goes 0 at 25 MHz
  val p_tick = RegInit(0.U(2.W))
  p_tick := p_tick + 1.U
  when(p_tick === 3.U){
    p_tick := 0.U}

  io.p_tick := p_tick


  //Create registers
  val x_count_reg, x_count_next = RegInit(0.U(10.W))
  val y_count_reg, y_count_next = RegInit(0.U(10.W))

  //output buffers
  val v_sync_reg, h_sync_reg = RegInit(0.U)
  val v_sync_next, h_sync_next = RegInit(0.U)

  //Reset all numbers if reset
  when (reset.asBool) {
    io.video_on := 0.U
    io.hsync := 0.U
    io.vsync := 0.U
    io.p_tick := 0.U
    io.x := 0.U
    io.y := 0.U

    x_count_reg := 0.U
    y_count_reg := 0.U
    x_count_next := 0.U
    y_count_next := 1.U
    v_sync_reg := 0.U
    v_sync_next := 0.U
    h_sync_reg := 0.U
    h_sync_next := 0.U

  }


  //x logic
  when (p_tick === 0.U) {


    when (x_count_reg === 799.U){
      x_count_next := 0.U
    } .otherwise {
      x_count_next := x_count_reg + 1.U
    }
  }

  //y logic
  when (p_tick === 0.U) {
    when (x_count_reg === 799.U) {
      when (y_count_reg === 524.U) {
        y_count_next := 0.U
      } .otherwise {
        y_count_next := y_count_reg + 1.U
     }
    }

  }
// Update registers
  x_count_reg := x_count_next
  y_count_reg := y_count_next


  // H_sync_next when in between retrace area
  h_sync_next := (x_count_reg > 656.U & x_count_reg < 752.U)

  v_sync_next := (y_count_reg > 513.U & y_count_reg < 515.U)

  //Video ON/OFF only On when pixels are in display area
  io.video_on := (x_count_reg < 640.U & y_count_reg < 480.U)

  //Update registers
  v_sync_reg := v_sync_next
  h_sync_reg := h_sync_next


  io.hsync := h_sync_reg
  io.vsync := v_sync_reg
  io.x := x_count_reg
  io.y := y_count_reg


}



object vga_controller extends App {
  emitVerilog(new vga_controller())
}


