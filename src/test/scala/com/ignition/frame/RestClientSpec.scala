package com.ignition.frame

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import com.ignition.types.{RichStructType, fieldToRichStruct, int, string}

@RunWith(classOf[JUnitRunner])
class RestClientSpec extends FrameFlowSpecification {

  val schema = string("city") ~ string("country")
  val grid = DataGrid(schema).addRow("london", "uk").addRow("atlanta", "us")

  //TODO: real calls to be replaced with mocks

  "RestClient" should {
    "return valid result and status" in {
      System.setProperty("weather_url", "http://api.openweathermap.org/data/2.5/weather")
      rt.vars("query") = "q"
      val url = "e{weather_url}?v{query}=${city},${country}"
      val client = RestClient(url, HttpMethod.GET, None, Map.empty, Some("result"), Some("status"), None)
      grid --> client

      assertSchema(schema ~ string("result") ~ int("status"), client, 0)
      client.output.collect.forall(!_.getString(2).isEmpty)
      client.output.collect.forall(_.getInt(3) == 200)
    }
    "return valid result and headers" in {
      val url = "http://api.openweathermap.org/data/2.5/weather?q=${city},${country}"
      val client = RestClient(url, HttpMethod.GET, None, Map.empty, Some("result"), None, Some("headers"))
      grid --> client

      assertSchema(schema ~ string("result") ~ string("headers"), client, 0)
      client.output.collect.forall(!_.getString(3).isEmpty)
    }
    "return failure code for bad path" in {
      val url = "http://api.openweathermap.org/data/2.5/unknown"
      val client = RestClient(url, HttpMethod.GET, None, Map.empty, None, Some("status"), None)
      grid --> client

      assertSchema(schema ~ int("status"), client, 0)
      client.output.collect.forall(_.getInt(2) >= 500)
    }
    "fail for unknown host" in {
      val url = "http://unknown_host_for_ignition_testing.org"
      val client = RestClient(url, HttpMethod.GET, None, Map.empty, None, Some("status"), None)
      grid --> client

      assertSchema(schema ~ int("status"), client, 0)
      client.output.collect must throwA[Exception]
    }
    "be unserializable" in assertUnserializable(RestClient("http://localhost", HttpMethod.GET, None, Map.empty))
  }
}