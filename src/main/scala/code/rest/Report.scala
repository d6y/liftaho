package code.rest

import net.liftweb.http.rest.RestHelper
import net.liftweb.http._
import java.io.{BufferedInputStream, FileInputStream}
import org.pentaho.reporting.libraries.resourceloader.ResourceManager
import java.net.URL
import org.pentaho.reporting.engine.classic.core.MasterReport
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil

object Report extends RestHelper {

  def init() {
    LiftRules.dispatch.append(Report)
  }

  serve {

    case Req("example" :: Nil, _, _) =>
      val manager = new ResourceManager
      manager.registerDefaults
      val reportPath = "file:reports/report.prpt"
      val res = manager.createDirectly(new URL(reportPath), classOf[MasterReport])
      val report = res.getResource.asInstanceOf[MasterReport]
      OutputStreamResponse(out => PdfReportUtil.createPDF(report, out))


    case Req("report" :: file :: Nil, _, _) =>
      val fileName = "output/" + file + ".pdf"
      val bis = new BufferedInputStream(new FileInputStream(fileName)) //TODO: check if file exists
      val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
      val ac = Stream(bArray)
      OutputStreamResponse(out => ac.foreach(out.write))
  }
}
