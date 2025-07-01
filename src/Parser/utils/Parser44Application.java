package Parser.utils;

import Parser.implementations.ChromeDriverSetup;
import Parser.implementations.Parser44.LitigationParser;
import Parser.implementations.Parser44.PurchaseParser44;
import Parser.implementations.TextFileResultsSaver;
import Parser.interfaces.DriverSetup;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser44Application {

    public static void main(String[] args) {
        List<String> selectedUrls = new ArrayList<>(Arrays.asList(
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18342761",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18344947",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18345067"
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18359859",
//"https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18364736",
//                    "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18359009"
//"https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18360709",
//"https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18350395"

//                    "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0330300051225000044"
//"https://zakupki.gov.ru/epz/order/notice/ezt20/view/common-info.html?regNumber=0372100054625000335"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0329400001725000037"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000031",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000022"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000032"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0172200002523000160",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0122300017023000012"
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17984400",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18049823"
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18015273",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18169394"
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0163200000325001831",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0131200001025002492",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0131200001025002520",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0131200001025002707",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0131200001025004235",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0131200001025005012",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0339300289625000030",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0320100003925000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200020425000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200005425000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0169300000325000385",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0140300024825000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200084225000024",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0130600040525000061",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825000126",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0815500000525008196",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0815500000525003398",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0112200000825002195",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0832200006625000139",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0832200006625000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0339300289625000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0339300289625000009",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100054625000335",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0838200000225000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0333400000325000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0333400000325000026",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200046825000017",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0318200030225000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0318200030225000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300008025000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100054625000371",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825002413",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0169300000325000410",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373400009625000015",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373400009625000057",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0815500000525004542",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0354200017425000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0354200017425000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000008",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0891200000625002178",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825000640",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0387200015025000012",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0320100018325000009",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0130600040525000046",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0364300108425000011",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0849400000225000067",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0849400000225000048",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0849400000225000042",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0152300044225000021",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200084225000012",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0329100005925000090",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0330300051225000044",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0108500000425000045",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0108500000425000970",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0387200010725000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358100009325000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0865200000325000224",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0184200000625000685",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0888500000225000136",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0108500000425001065",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0108500000425000888",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000040",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000023",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000039",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000036",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000012",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200045925000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100053425000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0335100016125000118",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0335100016125000092",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0163300029425000230",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301100021425000011",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0112200000825001574",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0162300009425000008",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373100089325000018",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373100089325000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054425000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054425000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0342300062525000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0818300019925000161",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0329200062225003889",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0122300013825000047",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0122300013825000042",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0123200000325000861",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825001084",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825001048",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0151100008425000025",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0151100008425000024",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0151100008425000019",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0112200000825000736",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0112200000825002420",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000026",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848300058125000126",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0324100004125000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0318100060325000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100054725000035",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348100011225000015",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0322400004025000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0187500000425000065",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0124200000625000279",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0107300010225000041",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301200077625000032",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301200077625000030",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0123300004625000078",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0123300004625000062",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301100000425000045",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0332200039325000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000008",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054425000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000020",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348100026525000110",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372500009325000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0375400000925000049",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0375400000925000042",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0375400000925000037",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0338100004825000016",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0187500000425000064",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0356500001425003294",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0138200003725000024",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0187300006525000385",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0353300009925000013",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372500009325000025",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0137200001225003242",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0137200001225001431",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200271025000062",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372500009325000020",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0361300021425000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000009",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200243825000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000025",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000011",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000042",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372500009325000008",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372500009325000018",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301200077625000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0136500001125002285",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0136500001125002947",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0190300000725000419",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0360300052825000349",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0816500000625009084",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0145200000425000608",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0145200000425000756",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0145200000425000439",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348200077025000023",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0387200021325000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0387200011125000020",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0387200011125000011",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054425000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0816500000625002427",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0190200000325007779",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0190200000325006095",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0134300044425000014",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0869200000225003335",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0123200000325001105",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0325100011425000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0190200000325001423",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100027325000224",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100027325000135",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100027325000084",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372100027325000037",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0329200062225003939",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0324400000825000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358300445725000011",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358300445725000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300003425000027",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348200080425000058",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373400006725000026",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0166300024725000424",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0373400006725000044",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0356500001425004169",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0130300016825000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825002601",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0816500000625009115",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319100001325000037",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0325100002425000006",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0169300008225000266",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200084225000018",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0153100006325000026",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200271025000007",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200271025000061",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0325100002425000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0845500001025000042",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0320100011225000138",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200084225000023",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0859200001125004111",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0171200001925000722",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0171200001925000877",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0818500000825001773",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0818500000825002876",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0860200000825001495",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0346300130525000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0818500000825003851",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0162300005325001153",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054225000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200236125000037",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348200085625000008",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848300047225000349",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348200077025000010",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0348200077025000009",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848600002725000108",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848600002725000109",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848600002725000100",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848300057125000063",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0848600002725000101",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200078925000054",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0142300006225000081",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0142300006225000072",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0142300006225000066",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0306100001225000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0318200048525000017",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0818500000825002462",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0118300013325000668",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200141825000038",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0372200236125000031",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0334100004725000002",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300325525000089",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300325525000115",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300325525000249",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0319300325525000165",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0112200000825003180",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0153300007625000035",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0318300165725000286",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0301300247625000338",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0162300015625000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0816600003725000019",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0173100005025000013",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0173100005025000009",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0380400000225000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200011325000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200020425000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054225000001",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054225000003",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054225000004",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0358200054225000005",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0119200000125011262",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0119200000125011763",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0119200000125006292",
//                "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=0119200000125009364"

                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18206304", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18205203", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18205204", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17931438", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18103190", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18040354", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18466619", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18443992", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18069321", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18154461", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18294451", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18384148", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18337748", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18404686", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18280501", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18394014", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17957656", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18008297", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18473973", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18212260", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18436400", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18259477", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17862371", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18447661", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18188599", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18141169", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18224158", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18367669", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18455392", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18154457", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18433184", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18043034", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18176870", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18127379", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18080813", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18472750", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18233789", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18433584", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18350395", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18120721", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18469482", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18081405", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18067407", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18090571", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17932206", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18096806", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17993849", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18167920", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18322536", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18255848", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18320758", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18113555", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18347424", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18013921", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18296709", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18349635", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18462859", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18378560", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17802638", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17980807", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18240038", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17839806", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18392555", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18452235", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18410543", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18346266", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18298735", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18211291", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18359859", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18048929", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17939109", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18049745", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18267091", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18294965", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18031476", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18116683", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17935093", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17953610", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17972300", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18287707", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18287878", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17843569", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18159971", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17938970", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18416844", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18465915", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18424288", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18207402", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18263025", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18108002", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18120607", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18273295", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18117484", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18123199", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18048144", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17828280", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17790930", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17873886", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18139086", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17946309", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18090091", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18462154", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18462618", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17973786", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18267431", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18272625", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18360709", "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18368087"
        ));
//
//        List<String> urlSlice = null;
//        try {
//            // Чтение файла
//            String content = new String(Files.readAllBytes(Paths.get("src/Nigger.txt")));
//
//            // Разделение ссылок и добавление в список
//            String[] urls = content.split(",\\s*");
//            for (String url : urls) {
//                url = url.trim();
//                if (!url.isEmpty()) {
//                    selectedUrls.add(url);
//                }
//            }
//
//            // Получаем срез от 250 позиции до конца
//            int startIndex = 1000;
//
//            // Проверяем, что startIndex не превышает размер списка
//            if (startIndex >= selectedUrls.size()) {
//                System.out.println("В списке меньше 250 ссылок!");
//                return;
//            }
//
//            urlSlice = selectedUrls.subList(startIndex, selectedUrls.size());
//
//            System.out.println("Всего ссылок: " + selectedUrls.size());
//            System.out.println("Будет обработано ссылок: " + urlSlice.size());
//
//            selectedUrls.forEach(System.out::println);
//
//        } catch (IOException e) {
//            System.err.println("Ошибка при чтении файла: " + e.getMessage());
//        }

//        try {
//        HibernateUtil.initialize("src/config.json");
//        if (HibernateUtil.testConnection()) {
//            System.out.println("Database connection is working");
//        }
//            HibernateUtil.printDatabaseSchema();
//            HibernateUtil.initializeTestData();
//        } catch (Exception e) {
//            System.err.println("Database connection failed: " + e.getMessage());
//            System.exit(1);
//        }
//        ResultsSaver<PurchaseItem> saver = new TextFileResultsSaver();
        String userAgent = RandomUserAgent.getRandomUserAgent();
        DriverSetup chromeSetup = new ChromeDriverSetup(userAgent);
        PurchaseParser44 parser = new PurchaseParser44(chromeSetup);

        parser.parseUrlsParallel(
                new ArrayList<>(selectedUrls),
                result -> handleParseResult(result),
                8, null
        );
//        parser.parseSupplierStatuses();
//        parser.parseSupplierLitigations();
//        parser.cleanupDownloadDirectory();

    }

    private static void handleParseResult(PurchaseParser44.ParseResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.error != null) {
                System.err.println("Ошибка при парсинге URL: " + result.url);
                result.error.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Ошибка при парсинге: " + result.error.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            } else if (result.purchaseData != null) {
                System.out.println("Успешно распарсено: " + result.purchaseData);
            }
        });
    }
}
