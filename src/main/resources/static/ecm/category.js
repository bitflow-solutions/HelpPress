function getCategory(obj) {
	if (obj) {
		document.frm.action = "/api/v1/ecm/category/" + $(obj).attr("bf-category-id");
		console.log("" + $(obj).attr("bf-order") + " " + $(obj).attr("bf-category-id") + " " + $(obj).attr("bf-name"));
		$(".btn-create").hide();
		$(".btn-modify").show();
		$(".btn-delete").show();
		$("#orderNo").val($(obj).attr("bf-order"));
		$("#categoryId").val($(obj).attr("bf-category-id"));
		$("#name").val($(obj).attr("bf-name"));
		$("#categoryId").attr("readonly", true);
	} else {
		$(".btn-modify").hide();
		$(".btn-delete").hide();
		$(".btn-create").show();
		$("#orderNo").val("");
		$("#categoryId").val("");
		$("#name").val("");
		$("#categoryId").attr("readonly", false);
	}
}

$(function() {
	$(".btn-delete").click(function () {
		if (confirm("선택한 카테고리와 하위 컨텐츠들이 모두 삭제됩니다.\n정말 삭제하시겠습니까?")) {
			var url = "/api/v1/ecm/category/" + $("#categoryId").val();
			$.ajax({
				url: url,
				method: "DELETE",
				data: { }
			})
			.done(function(msg) {
				if (msg.status===200) {
					// 성공
					location.reload();
				}
			});
		}
	});
	$(".btn-modify").click(function () {
		var url = "/api/v1/ecm/category/" + $("#categoryId").val();
		$.ajax({
			url: url,
			method: "PUT",
			data: { name: $("#name").val(), orderNo: $("#orderNo").val() }
		})
		.done(function(msg) {
			if (msg.status===200) {
				// 성공
				location.reload();
			}
		});
	});
	$(".btn-create").click(function () {
		var url = "/api/v1/ecm/category/" + $("#categoryId").val();
		$.ajax({
			url: url,
			method: "POST",
			data: { name: $("#name").val(), orderNo: $("#orderNo").val() }
		})
		.done(function(msg) {
			if (msg.status===200) {
				// 성공
				location.reload();
			}
		});
	});
	$("#category-wrapper li").click(function() {
		$("#category-wrapper li").removeClass("on");
		$(this).addClass("on");
	});
});
