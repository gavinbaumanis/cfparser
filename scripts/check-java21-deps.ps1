# Requires JDK 21+ on PATH and Maven. Fails if any runtime dep class is newer than Java 21 (major > 65).
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$outDir = Join-Path $root 'target/depcheck-runtime'
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

mvn -pl cfml.dictionary,cfml.parsing -am install -DskipTests -q
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

mvn -pl cfml.parsing,cfml.dictionary -am dependency:copy-dependencies `
  "-DincludeScope=runtime" `
  "-DoutputDirectory=$outDir"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

function Get-JavaLabel([int]$major) {
  switch ($major) {
    45 { '~1.1' }; 46 { '~1.2' }; 47 { '~1.3' }; 48 { '~1.4' }
    49 { '~5' }; 50 { '~6' }; 51 { '~7' }; 52 { '~8' }
    53 { '~9' }; 54 { '~10' }; 55 { '~11' }; 56 { '~12' }
    57 { '~13' }; 58 { '~14' }; 59 { '~15' }; 60 { '~16' }
    61 { '~17' }; 65 { '~21' }; 67 { '~23' }; 68 { '~24' }
    default { "~major=$major" }
  }
}

$failed = $false
Add-Type -AssemblyName System.IO.Compression.FileSystem

Get-ChildItem -Path $outDir -Filter '*.jar' | Sort-Object Name | ForEach-Object {
  $jar = $_
  $maxMajor = 0
  $zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
  try {
    foreach ($entry in $zip.Entries) {
      if (-not $entry.FullName.EndsWith('.class')) { continue }
      if ($entry.FullName -like 'META-INF/*') { continue }
      $stream = $entry.Open()
      try {
        $buf = New-Object byte[] 8
        $read = $stream.Read($buf, 0, 8)
        if ($read -lt 8) { continue }
        if ($buf[0] -ne 0xCA -or $buf[1] -ne 0xFE -or $buf[2] -ne 0xBA -or $buf[3] -ne 0xBE) { continue }
        $major = ($buf[6] -shl 8) -bor $buf[7]
        if ($major -gt $maxMajor) { $maxMajor = $major }
      } finally { $stream.Dispose() }
    }
  } finally { $zip.Dispose() }

  $label = if ($maxMajor -eq 0) { 'no-classes' } else { Get-JavaLabel $maxMajor }
  Write-Host ("{0} -> {1} (major {2})" -f $jar.Name, $label, $maxMajor)
  if ($maxMajor -gt 65) {
    Write-Host ("FAIL: {0} requires Java newer than 21 (major {1})" -f $jar.Name, $maxMajor)
    $failed = $true
  }
}

if ($failed) { exit 1 }
Write-Host 'OK: all runtime dependency classes are Java 21 or older.'
exit 0
