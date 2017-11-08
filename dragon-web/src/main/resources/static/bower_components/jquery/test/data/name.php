<?php
error_reporting(0);
$wait = $_REQUEST['wait'];
if($wait) {
	sleep($wait);
}
$xml = $_REQUEST['xml'];
if($xml) {
	header("Content-type: text/xml");
	$result = ($xml == "5-2") ? "3" : "?";
	echo "<math><calculation>$xml</calculation><result>$result</result></math>";
	die();
}
$dsName = $_REQUEST['dsName'];
if($dsName == 'foo') {
	echo "bar";
	die();
} else if($dsName == 'peter') {
	echo "pan";
	die();
}

echo 'ERROR <script type="text/javascript">ok( true, "dsName.php executed" );</script>';
?>
