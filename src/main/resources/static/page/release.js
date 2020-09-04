$(function () {
	$("#btn-download-all").click(function() {
		var release = false;
		if (confirm("현재 버전을 배포 이력에 기록하시겠습니까?")) {
			release = true;
		}
		$(".spinner-border").show();
		$("#ifrm").attr("src", "/api/v1/ecm/release/all?release=" + release);
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 10000);
	});
	$("#btn-download-changed").click(function() {
		downloadChanged();
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


function downloadChanged() {
	if (confirm("변경내역을 다운로드 하시겠습니까?")) {
		alert('다운로드 준비에 몇 초 소요됩니다');
		$(".spinner-border").show();
		$("#ifrm").attr("src", "/api/v1/ecm/release/changed");
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 2000);
	}
}