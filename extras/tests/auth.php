<?php
$url = 'https://student-jwt.herokuapp.com/auth';
$data = array(
  "email" => "<email>",
  "password" => "<password>",
);
$postdata = json_encode($data);
$curl = curl_init($url);
curl_setopt($curl, CURLOPT_POST, 1);
curl_setopt($curl, CURLOPT_POSTFIELDS, $postdata);
curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
$result = curl_exec($curl);
$httpcode = curl_getinfo($curl, CURLINFO_HTTP_CODE);
curl_close($curl);
echo 'HTTP code: ' . $httpcode;
echo "\n";
if ($httpcode==400) {
  print_r($result);
} else {
  $json =json_decode($result);
  print_r(json_encode($json,JSON_PRETTY_PRINT));
  file_put_contents("jwt.txt", $json->token);
}
echo "\n";
?>
