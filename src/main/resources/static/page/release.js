$(function () {
	$("#btn-download-all").click(function() {
		downloadAll();
	});
	$("#btn-download-changed").click(function() {
		downloadChanged();
	});
	$("#btn-download-changed-by-me").click(function() {
		downloadChangedByMe();
	});
});

function downloadFromHistory(id) {
	if (confirm("해당 버전을 다운로드 하시겠습니까?")) {
		$(".spinner-border").show();
		$("#ifrm").attr("src", "/api/v1/ecm/release/all/" + id);
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 1000);
	}
}

function downloadAll() {
	var release = false;
	if (confirm("현재 다운로드 버전을 배포 처리로 기록하시겠습니까?")) {
		release = true;
	}
	$(".spinner-border").show();
	$("#ifrm").attr("src", "/api/v1/ecm/release/all?release=" + release);
	setTimeout(function() {
		$(".spinner-border").hide();
	}, 12000);
}

function downloadChanged() {
	var release = false;
	if (confirm("현재 다운로드 버전을 배포 처리로 기록하시겠습니까?")) {
		release = true;
	}
	alert('다운로드에 몇 초 소요됩니다');
	$(".spinner-border").show();
	$("#ifrm").attr("src", "/api/v1/ecm/release/changed?release=" + release);
	setTimeout(function() {
		$(".spinner-border").hide();
	}, 3000);
}

function downloadChangedByMe() {
	alert('다운로드에 몇 초 소요됩니다');
	$(".spinner-border").show();
	$("#ifrm").attr("src", "/api/v1/ecm/release/changedbyme");
	setTimeout(function() {
		$(".spinner-border").hide();
	}, 3000);
}