$s1 = [xml]$raw_data
$s1.'rawdata'.'div'.'div'.'ul'.'li'.'div'.'h3'.'a' | ForEach-Object {
  Write-Output ($_.GetAttribute('title'))
  Write-Output ($_.GetAttribute('href'))
  $_ | Get-Member | Out-Null
}
