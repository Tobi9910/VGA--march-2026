import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ControllerTest extends AnyFlatSpec with ChiselScalatestTester {
  "Controller" should "increment r and wrap at 3" in {
    test(new vga_controller) { dut =>
      // Step through several cycles and print r
      dut.clock.setTimeout(10000)
      dut.io.reset.poke(1.U)   // assert reset
      dut.clock.step()         // hold for 1 cycle
      dut.io.reset.poke(0.U)   // deassert reset
      dut.clock.step(1000)


      for (i <- 0 until 20) {
        val value = dut.io.x.peek().litValue
        println(s"Cycle $i: x = $value")
        val value2 = dut.io.y.peek().litValue
        println(s"Cycle $i: y = $value2")
        val value3 = dut.io.hsync.peek().litValue
        println(s"Cycle $i: hsync = $value3")
        val value4 = dut.io.vsync.peek().litValue
        println(s"Cycle $i: vsync = $value4")
        dut.clock.step()
      }
      val value5 = dut.io.video_on.peek().litValue
      println(s" video = $value5")




    }
  }
}